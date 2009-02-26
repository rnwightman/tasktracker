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

package tasktracker.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Engine provides an API for various user interfaces to manage tasks.
 */
public class Engine {
    /**
     * Name of the file that this was loaded from
     */
    protected String fileName;
    
    /**
     * Set of the tasks
     */
    private SortedSet<Task> tasks;
    
    /**
     * Formatter used to read and write date members from the save file.
     */
    private SimpleDateFormat dateFormatter;
    
    /**
     * Create a new Engine, loading the task information from the specified
     * file.
     *
     * @param   fileName    name of the file to load history from
     */
    public Engine(String fileName)
            throws IOException{
        this.fileName = fileName.toString();
        tasks = new TreeSet<Task>();
        dateFormatter = new SimpleDateFormat("yyyy.MM.dd HHmmssZ");
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String line = null;
            
            while ((line = in.readLine()) != null) {
                if (line.length() == 0) continue;
                
                try {
                    Task task = deserializeTask(line);
                    tasks.add(task);
                }
                catch (ParseException parseEx) {
                    throw new IOException("Unable to parse task. Line: " + line + "; Exception: " + parseEx);
                }
            }
            
            in.close();
        }
        catch (FileNotFoundException fnfException) {
            File file = new File(fileName);
            file.createNewFile();
        }
    }
    
    /**
     * Save the current state of the engine. The same file as the engine was
     * created with will be used.
     *
     * @throws  IOException if there is a problem writing to file
     */
    public void saveState()
        throws IOException {
     
        // Create an empty file to save the state into
        File file = new File(this.fileName);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        
        try {    
            BufferedWriter out = new BufferedWriter(new FileWriter(this.fileName));
            
            for (Task task : this.tasks) {
                out.write(serializeTask(task));
                out.newLine();
            }
            
            out.close();
        }
        catch (FileNotFoundException fnfException) {
            // Should not occur
        }
    }
    
    private Task deserializeTask(String serialized) throws ParseException {
        String[] tokens = serialized.split(";");
        
        Date start = dateFormatter.parse(tokens[0]);
        String name = tokens[1];
        
        Date end = null;
        if (tokens.length >= 3) {
            end = dateFormatter.parse(tokens[2]);
        }
        
        if (end != null) {
            return new Task(name, start, end);
        }
        else {
            return new Task(name, start);
        }
    }
    
    private String serializeTask(Task task) {
        StringBuilder builder = new StringBuilder();
        
        builder.append(dateFormatter.format(task.getStart()));
        builder.append(";");
        
        builder.append(task.getTaskName());
        builder.append(";");
        
        if (task.isCompleted()) {
            builder.append(dateFormatter.format(task.getEnd()));
            builder.append(";");
        }
        
        return builder.toString();
    }
    
    /**
     * Add a new task to the set of existing tasks. If the previous task has
     * not been completed it will be using the start time of this task as its
     * end time.
     *
     * @param   task    task to add
     */
    public void addTask(Task task) {
        completeCurrentTask(task.getStart());
        
        this.tasks.add(task);
    }
    
    /**
     * Complete the current (the most recent) task.
     *
     * @param   completionTime  the date-time to complete the task with
     * @return  whether a task was completed
     */
    public boolean completeCurrentTask(Date completionTime) {
        // Check whether there is a previous task
        if (this.tasks.isEmpty() == false) {
            Task previousTask = this.tasks.last();
            
            // Complete the task if it is not completed
            if (previousTask.isCompleted() == false) {
                previousTask.complete(completionTime);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get the set of all tasks.
     *
     * @return  all tasks
     */
    public SortedSet<Task> getTasks() {
        return getTasksSince(new Date(0));
    }
    
    /**
     * Get the set of tasks that have occurred since the specified date.
     *
     * @param   earliest    the earliest date of the tasks to be retrieved
     * @return  all tasks that have occurred since the specified date
     */
    public SortedSet<Task> getTasksSince(Date earliest) {
        SortedSet<Task> recentTasks = new TreeSet<Task>();
        
        for (Task task : tasks) {
            if (task.getStart().after(earliest)) {
                recentTasks.add(task);
            }
        }
        
        return recentTasks;
    }
}