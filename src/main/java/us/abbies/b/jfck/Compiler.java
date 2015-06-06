package us.abbies.b.jfck;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;

public class Compiler {
    public static void main(String[] args) throws IOException {
        switch (args.length) {
        case 0:
            usage();
        case 1:
            Files.write(Paths.get("aout.class"), compile(readCode(args[0])));
            break;
        case 2:
            if (args[0].equals("-i")) {
                byte[] bytes = compile(readCode(args[1]));
                new ClassLoader() {{
                    try {
                        Object o = defineClass("aout", bytes, 0, bytes.length).newInstance();
                        ((Runnable) o).run();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }};
            } else {
                usage();
            }
            break;
        default:
            usage();
        }
    }

    private static String readCode(String path) {
        try {
            if (path.equals("-")) {
                StringBuilder sb = new StringBuilder();
                InputStreamReader r = new InputStreamReader(System.in, StandardCharsets.UTF_8);
                int ch;
                while ((ch = r.read()) != -1) {
                    sb.appendCodePoint(ch);
                }
                return sb.toString();
            } else {
                return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void usage() {
        System.out.println("usage: [-i] <filename|->");
    }

    public static byte[] compile(String code) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        writer.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "aout", null, "us/abbies/b/jfck/CodeBase", null);

        MethodVisitor init = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, "us/abbies/b/jfck/CodeBase", "<init>", "()V", false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(0, 0);

        MethodVisitor main = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        main.visitParameter("args", 0);
        main.visitTypeInsn(Opcodes.NEW, "aout");
        main.visitInsn(Opcodes.DUP);
        main.visitMethodInsn(Opcodes.INVOKESPECIAL, "aout", "<init>", "()V", false);
        main.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "aout", "run", "()V", false);
        main.visitInsn(Opcodes.RETURN);
        main.visitMaxs(0, 0);

        Deque<Label> branchStack = new ArrayDeque<>();
        MethodVisitor run = writer.visitMethod(Opcodes.ACC_PUBLIC, "run", "()V", null, null);
        code.chars().forEach(insn -> {
            switch (insn) {
            case '>':
                run.visitVarInsn(Opcodes.ALOAD, 0);
                run.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "us/abbies/b/jfck/CodeBase", "right", "()V", false);
                break;
            case '<':
                run.visitVarInsn(Opcodes.ALOAD, 0);
                run.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "us/abbies/b/jfck/CodeBase", "left", "()V", false);
                break;
            case '+':
                run.visitVarInsn(Opcodes.ALOAD, 0);
                run.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "us/abbies/b/jfck/CodeBase", "inc", "()V", false);
                break;
            case '-':
                run.visitVarInsn(Opcodes.ALOAD, 0);
                run.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "us/abbies/b/jfck/CodeBase", "dec", "()V", false);
                break;
            case '.':
                run.visitVarInsn(Opcodes.ALOAD, 0);
                run.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "us/abbies/b/jfck/CodeBase", "out", "()V", false);
                break;
            case ',':
                run.visitVarInsn(Opcodes.ALOAD, 0);
                run.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "us/abbies/b/jfck/CodeBase", "in", "()V", false);
                break;
            case '[':
                Label begin = new Label();
                Label end = new Label();
                run.visitLabel(begin);
                run.visitVarInsn(Opcodes.ALOAD, 0);
                run.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "us/abbies/b/jfck/CodeBase", "get", "()I", false);
                run.visitJumpInsn(Opcodes.IFEQ, end);
                branchStack.push(end);
                branchStack.push(begin);
                break;
            case ']':
                run.visitVarInsn(Opcodes.ALOAD, 0);
                run.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "us/abbies/b/jfck/CodeBase", "get", "()I", false);
                run.visitJumpInsn(Opcodes.IFNE, branchStack.pop());
                run.visitLabel(branchStack.pop());
                break;
            default:
            }
        });
        run.visitInsn(Opcodes.RETURN);
        run.visitMaxs(0, 0);

        writer.visitEnd();
        return writer.toByteArray();
    }
}
