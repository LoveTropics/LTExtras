package com.lovetropics.extras.client.screen.container;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.client.ClientCollectiblesList;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.network.message.ServerboundPickCollectibleItemPacket;
import com.lovetropics.extras.network.message.ServerboundReturnCollectibleItemPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

// Stop doing scrolling
// Containers were never meant to scroll
public class CollectibleBasketScreen extends AbstractContainerScreen<CollectibleBasketScreen.Menu> {
    private static final Component TITLE = ExtraItems.COLLECTIBLE_BASKET.get().getDescription();

    private static final ResourceLocation BACKGROUND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tab_items.png");
    private static final ResourceLocation CREATIVE_TABS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tabs.png");

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

    private float scroll;
    private boolean draggingScroller;
    private double dragOffsetY;

    public CollectibleBasketScreen(Inventory playerInventory) {
        super(new Menu(playerInventory.player, new CollectibleContainer(ClientCollectiblesList.get())), playerInventory, TITLE);
        playerInventory.player.containerMenu = menu;

        imageWidth = BACKGROUND_WIDTH;
        imageHeight = BACKGROUND_HEIGHT;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        menu.container.setScrollRowOffset(Math.round(clampScroll(scroll)));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        ScreenRectangle scroller = scrollerRectangle();
        if (scroller != null) {
            graphics.blit(CREATIVE_TABS_LOCATION, scroller.left(), scroller.top(), draggingScroller ? 244 : 232, 0, scroller.width(), scroller.height());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ScreenRectangle scroller = scrollerRectangle();
        if (scroller != null && mouseX >= scroller.left() && mouseX <= scroller.right() && mouseY >= scroller.top() && mouseY <= scroller.bottom()) {
            draggingScroller = true;
            dragOffsetY = scroller.top() - mouseY;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScroller) {
            float targetScrollerY = (float) (mouseY + dragOffsetY) - SCROLL_BAR_Y - topPos;
            scroll = clampScroll(targetScrollerY / (SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT) * maxScroll());
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        draggingScroller = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
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
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND_LOCATION, leftPos, topPos, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    @Nullable
    private ScreenRectangle scrollerRectangle() {
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
    protected void slotClicked(@Nullable Slot slot, int slotId, int mouseButton, ClickType type) {
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
        } else {
            simulateInventorySlotClicked(slot, slotId, mouseButton, type);
        }
    }

    private void tryPickCollectible(@NotNull Slot slot, CollectibleSlot collectibleSlot) {
        Collectible collectible = collectibleSlot.getCollectible();
        if (collectible != null) {
            menu.setCarried(slot.getItem().copy());
            PacketDistributor.sendToServer(new ServerboundPickCollectibleItemPacket(collectible));
        }
    }

    private void tryReturnCollectible(ItemStack carried) {
        Collectible carriedCollectible = Collectible.byItem(carried);
        if (carriedCollectible != null) {
            menu.setCarried(ItemStack.EMPTY);
            PacketDistributor.sendToServer(new ServerboundReturnCollectibleItemPacket(carriedCollectible));
        }
    }

    // We are the imposter
    private void simulateInventorySlotClicked(@Nullable Slot slot, int slotId, int mouseButton, ClickType type) {
        LocalPlayer player = minecraft.player;
        try {
            player.containerMenu = player.inventoryMenu;
            Slot mappedSlot = slot != null ? getSlotIn(slot, player.inventoryMenu) : null;
            int mappedSlotId = mappedSlot != null ? mappedSlot.index : slotId;
            minecraft.gameMode.handleInventoryMouseClick(player.inventoryMenu.containerId, mappedSlotId, mouseButton, type, player);
        } finally {
            player.containerMenu = menu;
        }
    }

    @Nullable
    private static Slot getSlotIn(Slot slot, AbstractContainerMenu menu) {
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

        @Nullable
        public Collectible getCollectible() {
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

        @Nullable
        public Collectible getCollectible(int slot) {
            List<Collectible> collectibles = list.collectibles();
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
