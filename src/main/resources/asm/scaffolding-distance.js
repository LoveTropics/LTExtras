function initializeCoreMod() {
    return {
        'scaffolding-distance': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.ScaffoldingBlock',
                'methodName': 'func_220117_a',
                'methodDesc': '(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;)I'
            },
            'transformer': function (method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
                
				for (i = 0, len = method.instructions.size(); i < len; i++) {
					var instr = method.instructions.get(i);
					if (instr.opcode == Opcodes.GETSTATIC && instr.name == 'SCAFFOLDING') {
						var next = instr.getNext();
						method.instructions.set(instr, new TypeInsnNode(Opcodes.INSTANCEOF, 'net/minecraft/block/ScaffoldingBlock'));
						method.instructions.set(next, new JumpInsnNode(Opcodes.IFEQ, next.label));
					}
				}
				
				return method;
            }
        }
    }
}