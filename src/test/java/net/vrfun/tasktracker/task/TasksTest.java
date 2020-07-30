/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;


import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
public class TasksTest {

    @Mock
    private TaskRepository taskRepository;

    private Tasks tasks;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        tasks = new Tasks(taskRepository);
    }

    @Test
    public void getAll() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.add(new Task("Task1"));
        allTasks.add(new Task("Task2"));

        doReturn(allTasks).when(taskRepository).findAll();

        assertThat(tasks.getAll()).hasSize(allTasks.size());
    }

    @Test
    public void getExistingTask() {
        doReturn(Optional.of(new Task())).when(taskRepository).findById(anyLong());

        assertThat(tasks.get(42L)).isNotNull();
    }

    @Test
    public void getNonExistingTask() {
        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        assertThat(tasks.get(42L)).isNull();
    }

    @Test
    public void createExistingTitle() {
        doReturn(Optional.of(new Task())).when(taskRepository).findTaskByTitle(anyString());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setTitle("MyTitle");
        try {
            tasks.create(taskEdit);
            fail("Failed to detect existing title during task creation.");
        }
        catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void createInvalidTitle() {
        doReturn(Optional.of(new Task())).when(taskRepository).findTaskByTitle(anyString());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        try {
            tasks.create(taskEdit);
            fail("Failed to detect missing title during task creation.");
        }
        catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void createSuccess() {
        doReturn(Optional.empty()).when(taskRepository).findTaskByTitle(anyString());
        doReturn(new Task("MyTitle")).when(taskRepository).save(any());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setTitle("MyTitle");

        try {
            Task newTask = tasks.create(taskEdit);

            assertThat(newTask.getTitle()).isEqualTo("MyTitle");
            assertThat(newTask.getDateClosed()).isNull();
            assertThat(newTask.getDateCreation()).isNotNull();
        }
        catch (IllegalArgumentException ignored) {
            fail("Failed to create a new task, reason: " + ignored.getMessage());
        }
    }

    @Test
    public void createOrCreateNewTask() {
        doReturn(Optional.of(new Task())).when(taskRepository).findTaskByTitle(anyString());

        assertThat(tasks.getOrCreate("TestTask1")).isNotNull();
        assertThat(tasks.getOrCreate("TestTask2")).isNotNull();
        assertThat(tasks.getOrCreate("TestTask3")).isNotNull();
    }

    @Test
    public void getOrCreateExistingTask() {
        doReturn(Optional.empty()).when(taskRepository).findTaskByTitle(anyString());
        doReturn(new Task()).when(taskRepository).save(any());

        assertThat(tasks.getOrCreate("TestTask1")).isNotNull();
        assertThat(tasks.getOrCreate("TestTask2")).isNotNull();
        assertThat(tasks.getOrCreate("TestTask3")).isNotNull();
    }

    @Test
    public void updateExistingTask() {
        Task existingTask = new Task("MyTitle");
        existingTask.setId(42L);
        existingTask.setDateCreation(Instant.now());
        existingTask.setDescription("MyTaskDescription");

        doReturn(Optional.of(existingTask)).when(taskRepository).findById(anyLong());
        doReturn(existingTask).when(taskRepository).save(any());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setId(42L);
        taskEdit.setDescription("MyDescriptionUpdated");
        taskEdit.setTitle("MyTitleUpdated");

        try {
            Task updatedTask = tasks.update(taskEdit);
            assertThat(updatedTask.getTitle()).isEqualTo("MyTitleUpdated");
            assertThat(updatedTask.getDescription()).isEqualTo("MyDescriptionUpdated");
            assertThat(updatedTask.getDateClosed()).isNull();
        }
        catch (IllegalArgumentException ignored) {
            fail("Failed to update a task");
        }
    }

    @Test
    public void closeTask() {
        Task existingTask = new Task("MyTitle");
        existingTask.setId(42L);

        doReturn(Optional.of(existingTask)).when(taskRepository).findById(anyLong());
        doReturn(existingTask).when(taskRepository).save(any());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setId(42L);
        taskEdit.setClosed(true);

        try {
            Task updatedTask = tasks.update(taskEdit);
            assertThat(updatedTask.getDateClosed()).isNotNull();
        }
        catch (IllegalArgumentException ignored) {
            fail("Failed to update a task");
        }
    }

    @Test
    public void updateNonExistingTask() {
        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        ReqTaskEdit taskEdit = new ReqTaskEdit();
        taskEdit.setId(42L);

        try {
            Task updatedTask = tasks.update(taskEdit);
            fail("Failed to detect a non-existing task during update.");
        }
        catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void updateTaskWithInvalidID() {
        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        ReqTaskEdit taskEdit = new ReqTaskEdit();

        try {
            Task updatedTask = tasks.update(taskEdit);
            fail("Failed to detect a non-valid task ID during update.");
        }
        catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void deleteExistingTask() {
        doReturn(Optional.of(new Task())).when(taskRepository).findById(anyLong());

        try {
            tasks.delete(42L);
        }
        catch (IllegalArgumentException ignored) {
            fail("Failed to delete a task");
        }
    }

    @Test
    public void deleteNonExistingTask() {
        doReturn(Optional.empty()).when(taskRepository).findById(anyLong());

        try {
            tasks.delete(42L);
            fail("Failed to detect non-existing task during deletion");
        }
        catch (IllegalArgumentException ignored) {
        }
    }
}