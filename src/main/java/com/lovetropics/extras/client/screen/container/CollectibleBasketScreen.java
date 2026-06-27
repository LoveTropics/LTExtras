package com.lovetropics.extras.client.screen.container;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientCollectiblesList;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.data.TropiCoinsStore;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.network.message.ServerboundExchangeTropiCoinsPacket;
import com.lovetropics.extras.network.message.ServerboundPickCollectibleItemPacket;
import com.lovetropics.extras.network.message.ServerboundReturnCollectibleItemPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import org.jspecify.annotations.Nullable;
import java.util.List;

// Stop doing scrolling
// Containers were never meant to scroll
public class CollectibleBasketScreen extends AbstractContainerScreen<CollectibleBasketScreen.Menu> {
    private static final Component TITLE = Util.make(() -> ExtraItems.COLLECTIBLE_BASKET.get().getName(ExtraItems.COLLECTIBLE_BASKET.asStack()));

    private static final Identifier BACKGROUND_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/creative_inventory/tab_items.png");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
    private static final Identifier TROPICOIN_SLOT_SPRITE = LTExtras.id("currency_slot");

    private static final SimpleContainer TROPICOIN_CONTAINER = new SimpleContainer(1);

    private static final int BACKGROUND_WIDTH = 195;
    private static final int BACKGROUND_HEIGHT = 136;

    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS = 9;
    private static final int ROWS = 5;

    private static final int SCROLL_BAR_X = 175;
    private static final int SCROLL_BAR_Y = 18;
    private static final int SCROLL_BAR_HEIGHT = 110;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;

    private static final int TROPICOIN_SLOT_X = -24;
    private static final int TROPICOIN_SLOT_Y = BACKGROUND_HEIGHT - 24;

    private float scroll;
    private boolean draggingScroller;
    private double dragOffsetY;

    private @Nullable Slot tropiCoinSlot;

    public CollectibleBasketScreen(Inventory playerInventory) {
        super(new Menu(playerInventory.player, new CollectibleContainer(ClientCollectiblesList.get())), playerInventory, TITLE, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        playerInventory.player.containerMenu = menu;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        menu.container.setScrollRowOffset(Math.round(clampScroll(scroll)));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        ScreenRectangle scroller = scrollerRectangle();
        if (scroller != null) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE, scroller.left(), scroller.top(), scroller.width(), scroller.height());
        }

        if (this.tropiCoinSlot == null) {
            this.tropiCoinSlot = new Slot(TROPICOIN_CONTAINER, 0, TROPICOIN_SLOT_X, TROPICOIN_SLOT_Y);
            this.getMenu().slots.add(this.tropiCoinSlot);
        }

        if (this.tropiCoinSlot != null) {
            if (Minecraft.getInstance().player != null) {
                if (this.isHovering(this.tropiCoinSlot.x, this.tropiCoinSlot.y, 16, 16, mouseX, mouseY)) {
                    graphics.setComponentTooltipForNextFrame(this.font, ExtraItems.TROPICOIN.asStack().getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.NORMAL), mouseX, mouseY);
                }
            }
        }
    }


    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        if (slot == this.tropiCoinSlot) {
            if (Minecraft.getInstance().player != null) {
                TropiCoinsStore data = Minecraft.getInstance().player.getData(ExtraAttachments.TROPICOINS_STORE);
                String text = data.getAmount() > 99 ? "99₊" : String.valueOf(data.getAmount()); //todo
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TROPICOIN_SLOT_SPRITE, slot.x - 8, slot.y - 8, 32, 32);
                graphics.fakeItem(ExtraItems.TROPICOIN.asStack(), slot.x, slot.y);
                graphics.itemDecorations(this.font, ExtraItems.TROPICOIN.asStack(), slot.x, slot.y, text);
            }
        }
        super.extractSlot(graphics, slot, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        ScreenRectangle scroller = scrollerRectangle();
        if (scroller != null && mouseX >= scroller.left() && mouseX <= scroller.right() && mouseY >= scroller.top() && mouseY <= scroller.bottom()) {
            draggingScroller = true;
            dragOffsetY = scroller.top() - mouseY;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        double mouseY = event.y();
        if (draggingScroller) {
            float targetScrollerY = (float) (mouseY + dragOffsetY) - SCROLL_BAR_Y - topPos;
            scroll = clampScroll(targetScrollerY / (SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT) * maxScroll());
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingScroller = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (canScroll()) {
            scroll = clampScroll(scroll - (float) pScrollY);
            return true;
        }
        return false;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0x404040, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_LOCATION, leftPos, topPos, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 256, 256, CommonColors.WHITE);
    }
    
    private @Nullable ScreenRectangle scrollerRectangle() {
        if (!canScroll()) {
            return null;
        }
        return new ScreenRectangle(leftPos + SCROLL_BAR_X, topPos + SCROLL_BAR_Y + scrollerY(), SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    private int scrollerY() {
        return Math.round(clampScroll(scroll) / maxScroll() * (SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT));
    }

    private float clampScroll(float scroll) {
        return Mth.clamp(scroll, 0.0f, maxScroll());
    }

    private boolean canScroll() {
        return maxScroll() > 0;
    }

    private int maxScroll() {
        return Math.max(menu.container.contentRows() - ROWS, 0);
    }
    
    @Override
    protected void slotClicked(@Nullable Slot slot, int slotId, int mouseButton, ContainerInput type) {
        if (slot instanceof CollectibleSlot collectibleSlot) {
            switch (type) {
                case PICKUP, PICKUP_ALL, QUICK_MOVE -> {
                    ItemStack carried = menu.getCarried();
                    if (carried.isEmpty()) {
                        tryPickCollectible(slot, collectibleSlot);
                    } else {
                        tryReturnCollectible(carried);
                    }
                }
                default -> {
                    // Should implement more, but this screen is already a horrible hack
                }
            }
        } else if (slot == this.tropiCoinSlot) {
            TropiCoinsStore data = Minecraft.getInstance().player.getData(ExtraAttachments.TROPICOINS_STORE);
            if (type == ContainerInput.PICKUP || type == ContainerInput.QUICK_CRAFT) {
                if (type == ContainerInput.QUICK_CRAFT) {
                    if (mouseButton == 5) {
                        mouseButton = 1;
                    } else if (mouseButton == 1) {
                        mouseButton = 0;
                    }
                }
                if (this.getMenu().getCarried().isEmpty()) {
                    if (data.getAmount() > 0) {
                        ItemStack stack = ExtraItems.TROPICOIN.asStack();
                        int maxStackSize = stack.getMaxStackSize();
                        int amount = 0;
                        if (mouseButton == 0) { // pick up stack
                            amount = Math.min(maxStackSize, data.getAmount());
                        } else if (mouseButton == 1) { // pick up half a stack
                            amount = Math.min(maxStackSize, data.getAmount()) / 2;
                        }
                        if (amount > 0) {
                            this.getMenu().setCarried(stack.copyWithCount(amount));
                            data.setAmount(data.getAmount() - amount);
                            ClientPacketDistributor.sendToServer(new ServerboundExchangeTropiCoinsPacket(false, amount * 2));
                        }
                    }
                } else if (this.getMenu().getCarried().is(ExtraItems.TROPICOIN)) {
                    ItemStack stack = this.getMenu().getCarried().copy();
                    int amount = 0;
                    if (mouseButton == InputConstants.MOUSE_BUTTON_LEFT) { // place carried stack
                        amount = stack.getCount();
                    } else if (mouseButton == InputConstants.MOUSE_BUTTON_RIGHT) { // place single item
                        amount = 1;
                    }
                    if (amount > 0) {
                        stack.shrink(amount);
                        data.setAmount(data.getAmount() + amount);
                        this.getMenu().setCarried(stack);
                        ClientPacketDistributor.sendToServer(new ServerboundExchangeTropiCoinsPacket(true, amount));
                    }
                }
            }
        } else {
            simulateInventorySlotClicked(slot, slotId, mouseButton, type);
        }
    }

    private void tryPickCollectible(Slot slot, CollectibleSlot collectibleSlot) {
        Holder<Collectible> collectible = collectibleSlot.getCollectible();
        if (collectible != null) {
            menu.setCarried(slot.getItem().copy());
            ClientPacketDistributor.sendToServer(new ServerboundPickCollectibleItemPacket(collectible));
        }
    }

    private void tryReturnCollectible(ItemStack carried) {
        Holder<Collectible> carriedCollectible = Collectible.byItem(carried);
        if (carriedCollectible != null) {
            menu.setCarried(ItemStack.EMPTY);
            ClientPacketDistributor.sendToServer(new ServerboundReturnCollectibleItemPacket(carriedCollectible));
        }
    }

    // We are the imposter
    private void simulateInventorySlotClicked(@Nullable Slot slot, int slotId, int mouseButton, ContainerInput type) {
        LocalPlayer player = minecraft.player;
        try {
            player.containerMenu = player.inventoryMenu;
            Slot mappedSlot = slot != null ? getSlotIn(slot, player.inventoryMenu) : null;
            int mappedSlotId = mappedSlot != null ? mappedSlot.index : slotId;
            minecraft.gameMode.handleContainerInput(player.inventoryMenu.containerId, mappedSlotId, mouseButton, type, player);
        } finally {
            player.containerMenu = menu;
        }
    }

    private static @Nullable Slot getSlotIn(Slot slot, AbstractContainerMenu menu) {
        for (Slot otherSlot : menu.slots) {
            if (slot.isSameInventory(otherSlot) && slot.getContainerSlot() == otherSlot.getContainerSlot()) {
                return otherSlot;
            }
        }
        return null;
    }

    public static class Menu extends AbstractContainerMenu {
        private final InventoryMenu inventoryMenu;
        private final CollectibleContainer container;

        protected Menu(Player player, CollectibleContainer container) {
            super(null, player.inventoryMenu.containerId);
            this.container = container;
            inventoryMenu = player.inventoryMenu;
            Inventory playerInventory = player.getInventory();

            for (int row = 0; row < ROWS; row++) {
                for (int column = 0; column < COLUMNS; column++) {
                    int index = column + row * COLUMNS;
                    addSlot(new CollectibleSlot(container, index, 9 + column * SLOT_SIZE, 18 + row * SLOT_SIZE));
                }
            }

            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column, 9 + column * SLOT_SIZE, 112));
            }
        }

        @Override
        public ItemStack quickMoveStack(Player player, int pIndex) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return player.isHolding(ExtraItems.COLLECTIBLE_BASKET.get());
        }

        @Override
        public ItemStack getCarried() {
            return inventoryMenu.getCarried();
        }

        @Override
        public void setCarried(ItemStack stack) {
            inventoryMenu.setCarried(stack);
        }
    }

    private static class CollectibleSlot extends Slot {
        private final CollectibleContainer container;

        public CollectibleSlot(CollectibleContainer container, int index, int x, int y) {
            super(container, index, x, y);
            this.container = container;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        public @Nullable Holder<Collectible> getCollectible() {
            return container.getCollectible(index);
        }
    }

    public static class CollectibleContainer implements Container {
        private final ClientCollectiblesList list;
        private int scrollRowOffset;

        public CollectibleContainer(ClientCollectiblesList list) {
            this.list = list;
        }

        public void setScrollRowOffset(int scrollRowOffset) {
            this.scrollRowOffset = scrollRowOffset;
        }

        @Override
        public int getContainerSize() {
            return ROWS * COLUMNS;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        public @Nullable Holder<Collectible> getCollectible(int slot) {
            List<Holder<Collectible>> collectibles = list.collectibles();
            int index = getIndexForSlot(slot);
            if (index >= 0 && index < collectibles.size()) {
                return collectibles.get(index);
            }
            return null;
        }

        @Override
        public ItemStack getItem(int slot) {
            List<ItemStack> stacks = list.itemStacks();
            int index = getIndexForSlot(slot);
            if (index >= 0 && index < stacks.size()) {
                return stacks.get(index);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return getItem(slot).copy();
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return getItem(slot).copy();
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
        }

        public int contentRows() {
            return Mth.positiveCeilDiv(list.collectibles().size(), COLUMNS);
        }

        private int getIndexForSlot(int slot) {
            return slot + scrollRowOffset * COLUMNS;
        }
    }
}
