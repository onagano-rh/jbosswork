package com.example;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class App {
    private static Logger LOG = Logger.getLogger(App.class.getName());
    private static boolean INTERACTIVE = System.console() != null;
    private static BufferedReader STDIN = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws Exception {
        LOG.info("Started.");
        if (INTERACTIVE) write("Type 'Ctrl-d' to quit, 'Ctrl-u' to clear.");
        boolean loop = true;
        HotRodClient program = null;
        while (loop) {
            String input = read("hoge> ");
            if (input == null) {
                loop = false;
            } else {
                String[] cmd  = input.split("\\s");
                try {
                    switch (cmd[0]) {

                    case "connect":
                        if (cmd.length > 2)
                            program = new HotRodClient(cmd[1], cmd[2]);
                        else
                            program = new HotRodClient(cmd[1], null);
                        break;

                    case "cache":
                        if (cmd.length > 1)
                            program.setCache(cmd[1]);
                        else
                            program.setCache(null);
                        break;

                    case "disconnect":
                        program.disconnect();
                        break;

                    case "get":
                        write(program.get(cmd[1]));
                        break;

                    case "getmeta":
                        write(program.getWithMetadata(cmd[1]));
                        break;

                    case "getBulk":
                        program.getBulk();
                        break;

                    case "put":
                        write(program.put(cmd[1], cmd[2]));
                        break;

                    case "putIfAbsent":
                        write(program.putIfAbsent(cmd[1], cmd[2]));
                        break;

                    case "remove":
                        write(program.remove(cmd[1]));
                        break;

                    case "sleep":
                        try {
                            long usec = Long.parseLong(cmd[1]);
                            Thread.sleep(usec);
                        } catch (Exception ignore) {}
                        break;

                    case "size":
                        write(program.size());
                        break;

                    case "keySetSize":
                        write(program.keySetSize());
                        break;

                    case "clear":
                        program.clear();
                        break;

                    case "clearAsync":
                        program.clearAsync();
                        break;

                    case "addListener":
                        program.addListener();
                        break;

                    case "removeListener":
                        program.removeListener();
                        break;

                    default:
                        break;
                    }
                } catch (Exception e) {
                    write("Something wrong: " + e.getMessage());
                    LOG.log(Level.FINE, "Something wrong", e);
                }
            } // end else
        } // end while
        LOG.info("Finished.");
    }

    private static String read(String prompt) {
        if (INTERACTIVE) System.out.print(prompt);
        String line = null;
        try {
            line = STDIN.readLine();
        } catch (Exception ignore) {}
        return line;
    }

    private static void write(Object mesg) {
        System.out.println(mesg);
    }
}
