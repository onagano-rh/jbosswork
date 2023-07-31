package com.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Usage:
 *   mvn exec:java -Dexec.mainClass=com.example.App
 */
public class App {
    private static final Logger LOG = LogManager.getLogger(App.class);
    private static boolean INTERACTIVE = System.console() != null;
    private static BufferedReader STDIN = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws Exception {
        LOG.info("Started.");
        if (INTERACTIVE) write("Type 'Ctrl-d' to quit, 'Ctrl-u' to clear.");
        boolean loop = true;
        HotRodClient program = new HotRodClient("localhost:11222", "hogeCache");
        while (loop) {
            String input = read("hoge> ");
            if (input == null) {
                loop = false;
            } else {
                String[] cmd  = input.split("\\s");
                try {
                    switch (cmd[0]) {

                    // case "connect":
                    //     if (cmd.length > 2)
                    //         program = new HotRodClient(cmd[1], cmd[2]);
                    //     else
                    //         program = new HotRodClient(cmd[1], null);
                    //     break;

                    // case "cache":
                    //     if (cmd.length > 1)
                    //         program.setCache(cmd[1]);
                    //     else
                    //         program.setCache(null);
                    //     break;

                    case "disconnect":
                        program.disconnect();
                        break;

                    case "get":
                        write(program.get(cmd[1]));
                        break;

                    case "getmeta":
                        write(program.getWithMetadata(cmd[1]));
                        break;

                    case "put":
                        write(program.put(cmd[1], cmd[2]));
                        break;

                    case "putNull":
                        write(program.put(cmd[1], null));
                        break;

                    case "containsKey":
                        write(program.containsKey(cmd[1]));
                        break;

                    case "putIfAbsent":
                        write(program.putIfAbsent(cmd[1], cmd[2]));
                        break;

                    case "putWithExpiration":
                        write(program.putWithExpiration(cmd[1], cmd[2], Integer.parseInt(cmd[3])));
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

                    default:
                        break;
                    }
                } catch (Exception e) {
                    write("Something wrong: " + e.getMessage());
                    LOG.error("Something wrong", e);
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
