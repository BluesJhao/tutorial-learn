package org.self.learn.design.pattern.behavior;

import java.util.Scanner;

/**
 * 状态模式.
 * <p>
 * 对象持有不同状态时，封装对应不同的行为
 * 将对象的行为封装到具体的类，通过上下文切换状态，然后处理具体的行为
 * 通常用于有状态的对象，关键在于状态的切换，易于新增状态与行为
 * <p>
 * 实现一个聊天机器人，机器人有在线和不在线状态
 *
 * @date 2020-04-30.
 */
public class StateSample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MachineContext machine = new MachineContext();
        for (; ; ) {
            System.out.print(">:");
            String msg = scanner.nextLine();
            String chat = machine.chat(msg);
            System.out.println(chat == null || chat.isEmpty() ? "no reply" : "<:" + chat);
        }
    }

    private static class MachineContext {

        State state = new OfflineState();

        public String chat(String msg) {
            if ("hello".equalsIgnoreCase(msg)) {
                state = new OnlineState();
                return state.init();
            } else if ("bye".equalsIgnoreCase(msg)) {
                state = new OfflineState();
                return state.init();
            }
            return state.replay(msg);
        }

    }

    /**
     * 具体下线状态对应具体下线行为
     */
    private static class OfflineState implements State {
        @Override
        public String init() {
            return "bye!";
        }

        @Override
        public String replay(String input) {
            return "";
        }
    }

    /**
     * 具体在线状态对应具体在线行为
     */
    private static class OnlineState implements State {
        @Override
        public String init() {
            return "hello!";
        }

        @Override
        public String replay(String input) {
            return "yes, " + input;
        }
    }

    /**
     * 状态对应行为
     */
    private interface State {
        String init();

        String replay(String input);
    }
}


