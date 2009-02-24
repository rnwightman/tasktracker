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

import java.util.*;

/**
 * A Task object encapsulates the details of a task carried out for a period of
 * time.
 *
 * @author  Ryan Wightman
 */
public class Task implements java.lang.Comparable {
    private String taskName;
    private Date start;
    private Date end;
    private boolean completed;
    
    /**
     * Create a named task with a specified start date-time.
     *
     * @param   taskName    the name of the task
     * @param   start   the date-time that the task was started
     */
    public Task(String taskName, Date start) {
        this(taskName, start, start);
        completed = false;
    }
    
    /**
     * Create a named task with specified start and end date-times.
     *
     * @param   taskName    the name of the task
     * @param   start   the date-time that the task was started
     * @param   end     the date-time that the task was completed
     */
    public Task(String taskName, Date start, Date end) {
        this.taskName = taskName;
        this.start = start;
        this.end = end;
        this.completed = true;
    }
    
    public int compareTo(Object o) {
        if (o instanceof Task) {
            Task other = (Task)o;
            return this.getStart().compareTo(other.getStart());
        }
        else {
            throw new ClassCastException();
        }
    }
    
    /**
     * Gets the name of this task.
     *
     * @return  the name of this task
     */
    public String getTaskName() {
        return this.taskName.toString();
    }
    
    public Date getStart() {
        return (Date)this.start.clone();
    }
    
    public Date getEnd() {
        return (Date)this.end.clone();
    }
    
    /**
     * Record the completion of the task at the current date-time.
     */
    public void complete() {
        complete(new Date());
    }
    
    /**
     * Record the complection of the task at the specified date-time.
     *
     * @param   end the date-time that the task was completed
     */
    public void complete(Date end) {
        if (this.completed) {
            throw new IllegalStateException("Task already completed.");
        }
        
        if (end.before(this.start)) {
            throw new IllegalArgumentException("Completion date cannot be before the start date.");
        }
        
        this.completed = true;
        this.end = end;
    }
    
    /**
     * Checks whether this <code>Task</code> has completed.
     *
     * @return true when a completion time has been specified, otherwise false
     */
    public boolean isCompleted() {
        return this.completed;
    }
    
    /**
     * Gets the duration of the task in minutes. If the task is not yet
     * completed the current time will be used in the calcuation.
     *
     * @return number of minutes between the start and end time
     */
    public double getDurationInMinutes() {
        Date completed = new Date();
        if (this.isCompleted()) completed = this.end;
        
        long durationInMilliseconds = completed.getTime() - this.start.getTime();
        double durationInMinutes = durationInMilliseconds / 60000;
        
        return durationInMinutes;
    }
}
