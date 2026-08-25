package com.ting.learn;

/**
 * A1.3 练习：程序结构、main、变量、null。
 * <p>
 * 怎么跑 / 调试（IDEA）：
 * 1. 打开本文件 → 左侧行号旁点红点打断点（建议先打在 main 第一行）
 * 2. 右键本类 → Debug 'BasicsDebugDemo.main()'（不要用 Spring Boot 启动）
 * 3. F8 单步跳过 / F7 进入方法 / F9 继续到下一断点
 * 4. 看右侧 Variables：变量名、当前值、类型
 */
public class BasicsDebugDemo {

    public static void main(String[] args) {
        // —— 变量：先声明并赋值再使用 ——
        int age = 18;
        double score = 95.5;
        boolean passed = true;
        char grade = 'A';
        String name = "Ting";

        // + ：两边都是数字 → 加法；有一边是字符串 → 拼接
        int nextYear = age + 1;
        String intro = name + " age=" + age + ", next=" + nextYear;

        System.out.println(intro);
        System.out.println("score=" + score + ", passed=" + passed + ", grade=" + grade);

        // —— 常量：赋值后不可改 ——
        final int MAX_SCORE = 100;
        System.out.println("MAX_SCORE=" + MAX_SCORE);

        // —— null：引用类型可以暂时不指向对象 ——
        String nickname = null;
        System.out.println("nickname == null ? " + (nickname == null));

        // 想看 NPE：取消下一行注释，Debug 时会停在异常处
        // System.out.println(nickname.length());

        nickname = "小T";
        System.out.println("nickname.length()=" + nickname.length());

        // 练习：改 age / name，单步看 Variables 面板值如何变
        printSummary(name, age, score);
    }

    /** 单独方法方便练习 F7「步入」 */
    private static void printSummary(String name, int age, double score) {
        System.out.println("--- summary ---");
        System.out.println(name + " / " + age + " / " + score);
    }
}
