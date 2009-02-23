/*
    TaskTracker is a utility to track and report time spent on tasks.
    
    This file is part of TaskTracker

    TaskTracker is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TaskTracker is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TaskTracker.  If not, see <http://www.gnu.org/licenses/>.
*/

package tasktracker.cli;

import tasktracker.core.Engine;
import tasktracker.core.Task;
import java.util.*;

public class CommandLineInterface {
    
    private static String getAttribute(String args[], String key) {
        for (int i=0; i < args.length; i++) {
            if (key.equals(args[i])) {
                return args[i+1];
            }
        }
        
        return null;
    }
    
    private static String getCommand(String[] args) {
        for (int i=0; i < args.length; i++) {
            if (args[i].startsWith("-") == false) {
                return args[i];
            }
            else i++;
        }
        
        return null;
    }
    
    private static String[] getCommandArguments(String[] args, String command) {
        for (int i=0; i < args.length; i++) {
            if (args[i].equals(command)) {
                String[] commandArgs = new String[args.length - i - 1];
                System.arraycopy(args, i + 1, commandArgs, 0, args.length - 1 - i);
                return commandArgs;
            }
        }
        
        return new String[0];
    }
    
    private static Task createTaskForAdd(String[] args) {
        String taskName = new String();
        if (args.length >= 1) {
            taskName = args[0];
        }
        
        Date start = new Date();
        // TODO: Parse date from args
        
        return new Task(taskName, start);
    }
    
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("Usage: <command>");
                return;
            }
            
            String fileName = getAttribute(args, "-file");
            if (fileName == null) fileName = "tasktracker.data";
            
            String command = getCommand(args);
            String[] commandArgs = getCommandArguments(args, command);
            
            System.out.println("Command: " + command + " Args: " + commandArgs.length);
            
            Engine engine = new Engine(fileName);
            boolean saveAfterCommand = false;
            
            // Handle the commands
            if ("add".equalsIgnoreCase(command) ||
                "a".equalsIgnoreCase(command)) {
                saveAfterCommand = true;
                
                Task task = createTaskForAdd(commandArgs);
                engine.addTask(task);
            }
            
            else {
                System.out.println("Unknown command " + command);
            }
            
            // Save if desired by the command
            if (saveAfterCommand) engine.saveState();
        }
        
        catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }
}
