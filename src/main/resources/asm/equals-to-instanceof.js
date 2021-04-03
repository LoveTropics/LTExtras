function initializeCoreMod() {
    return {
        'block-cannotattach': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.Block',
                'methodName': 'func_220073_a', // cannotAttach
                'methodDesc': '(Lnet/minecraft/block/Block;)Z'
            },
            'transformer': fixBarrier
        }
    }
}

function fixBarrier(method) {
    //                                         Blocks.BARRIER
    return redirectEqualsToInstanceof(method, 'field_180401_cv', 'net/minecraft/block/BarrierBlock');
}

function redirectEqualsToInstanceof(method, field, type) {
    var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
    
    for (i = 0, len = method.instructions.size(); i < len; i++) {
        var instr = method.instructions.get(i);
        // Find static reference to given field
        if (instr.opcode == Opcodes.GETSTATIC && instr.name == ASM.mapField(field)) {
            var next = instr.getNext();
            // Replace with an instanceof check for the given type
            method.instructions.set(instr, new TypeInsnNode(Opcodes.INSTANCEOF, type));
            // Convert the jump instruction to the appropriate matching opcode for instanceof checks (inverted for some reason?)
            //   IF_ACMPEQ -> IFNE
            //   IF_ACMPNE -> IFEQ
            method.instructions.set(next, new JumpInsnNode(next.opcode == Opcodes.IF_ACMPEQ ? Opcodes.IFNE : Opcodes.IFEQ, next.label));
        }
    }
    
    return method;
}
