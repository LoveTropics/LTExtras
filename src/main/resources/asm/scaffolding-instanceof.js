function initializeCoreMod() {
    return {
        'scaffolding-distance': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.ScaffoldingBlock',
                'methodName': 'func_220117_a',
                'methodDesc': '(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;)I'
            },
            'transformer': fixBlockCheck
        },
        'entity-move': {
        	'target': {
        		'type': 'METHOD',
        		'class': 'net.minecraft.entity.Entity',
        		'methodName': 'func_213315_a', // move
        		'methodDesc': '(Lnet/minecraft/entity/MoverType;Lnet/minecraft/util/math/Vec3d;)V'
        	},
        	'transformer': fixBlockCheck
        },
        'livingentity-climbable': {
        	'target': {
        		'type': 'METHOD',
        		'class': 'net.minecraft.entity.LivingEntity',
        		'methodName': 'func_213362_f', // handleOnClimbable
        		'methodDesc': '(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;'
        	},
        	'transformer': fixBlockCheck
        }
    }
}

function fixBlockCheck(method) {
    var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
    
	for (i = 0, len = method.instructions.size(); i < len; i++) {
		var instr = method.instructions.get(i);
		if (instr.opcode == Opcodes.GETSTATIC && instr.name == ASM.mapField('field_222420_lI')) { // Blocks.SCAFFOLDING
			var next = instr.getNext();
			method.instructions.set(instr, new TypeInsnNode(Opcodes.INSTANCEOF, 'net/minecraft/block/ScaffoldingBlock'));
			method.instructions.set(next, new JumpInsnNode(next.opcode == Opcodes.IF_ACMPEQ ? Opcodes.IFNE : Opcodes.IFEQ, next.label));
		}
	}
	
	return method;
}