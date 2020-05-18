/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2020 Oliver Selinger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package os.failsafe.executor;

import os.failsafe.executor.task.TaskId;
import os.failsafe.executor.task.TaskDefinition;

class Execution {

    private final TaskDefinition taskDefinition;
    private final PersistentTask persistentTask;

    Execution(TaskDefinition taskDefinition, PersistentTask persistentTask) {
        this.taskDefinition = taskDefinition;
        this.persistentTask = persistentTask;
    }

    public TaskId perform() {
        try {
            taskDefinition.execute(persistentTask.getParameter());

            notifySuccess();

            persistentTask.remove();
        } catch (Exception e) {
            persistentTask.fail(e);

            notifyFailed();
        }

        return persistentTask.getId();
    }

    private void notifySuccess() {
        taskDefinition.allListeners().forEach(listener -> listener.succeeded(persistentTask.getName(), persistentTask.getId(), persistentTask.getParameter()));
    }

    private void notifyFailed() {
        taskDefinition.allListeners().forEach(listener -> listener.failed(persistentTask.getName(), persistentTask.getId(), persistentTask.getParameter()));
    }
}
