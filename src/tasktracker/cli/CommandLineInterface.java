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
import java.text.DateFormat;
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
        else {
            throw new IllegalArgumentException("Must provide a task name");
        }
        
        Date start = new Date();
        // TODO: Parse date from args
        
        return new Task(taskName, start);
    }
    
    private static String formatMinutes(double minutes) {
        int hours = (int)(minutes / 60.0);
        int remainingMinutes = (int)(minutes % 60.0);
        
        return String.format("%1$d:%2$02d", hours, remainingMinutes);
    }
    
    private static String generateReport(SortedSet<Task> tasks) {
        TreeMap<Calendar, TreeMap<String, Double>> report = new TreeMap<Calendar, TreeMap<String, Double>>();
        
        // tabulate report
        for (Task task : tasks) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(task.getStart());
            
            // determine the date (without time)
            Calendar date = Calendar.getInstance();
            date.clear();
            date.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            
            if (!report.containsKey(date)) {
                report.put(date, new TreeMap<String, Double>(String.CASE_INSENSITIVE_ORDER));
            }
            
            TreeMap<String, Double> durationsByTaskName = report.get(date);
            double currentDuration = 0.0;
            if (durationsByTaskName.containsKey(task.getTaskName())) {
                currentDuration = durationsByTaskName.get(task.getTaskName()).doubleValue();
            }
            
            currentDuration = currentDuration + task.getDurationInMinutes();
            durationsByTaskName.put(task.getTaskName(), currentDuration);
        }
        
        // generate report
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL);
        
        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        
        // Loop through all the dates in the task set
        for (Calendar date : report.keySet()) {
            TreeMap<String, Double> durationsByTaskName = report.get(date);
            double totalMinutes = 0.0;
        
            // Write daily header    
            sb.append(dateFormatter.format(date.getTime()));
            sb.append(newLine);
            
            // Write out each task
            for (String taskName : durationsByTaskName.keySet()) {
                double durationInMinutes = durationsByTaskName.get(taskName);
                totalMinutes += durationInMinutes;
                
                sb.append(taskName + ": " + formatMinutes(durationInMinutes));
                sb.append(newLine);
            }
            
            // Write out the daily total
            sb.append("Total: " + formatMinutes(totalMinutes) + newLine);
            sb.append(newLine);
        }
        
        return sb.toString();
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
            
            Engine engine = new Engine(fileName);
            boolean saveAfterCommand = false;
            
            // Handle the commands
            if ("start".equalsIgnoreCase(command) ||
                "s".equalsIgnoreCase(command)) {
                saveAfterCommand = true;
                
                Task task = createTaskForAdd(commandArgs);
                engine.addTask(task);
            }
            
            else if ("complete".equalsIgnoreCase(command) ||
                     "c".equalsIgnoreCase(command)) {
                boolean taskCompleted = engine.completeCurrentTask(new Date());
                if (taskCompleted) {
                    saveAfterCommand = true;
                }
                else {
                    System.err.println("Previous task is not incomplete.");
                }
            }
            
            else if ("report".equalsIgnoreCase(command) ||
                     "r".equalsIgnoreCase(command)) {
                SortedSet<Task> tasks = engine.getTasks();
                String report = generateReport(tasks);
                
                System.out.println(report);
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
