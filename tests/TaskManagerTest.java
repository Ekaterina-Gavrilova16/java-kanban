import managers.TaskManager;
import managers.impl.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    protected Task createTask() {
        return new Task("Задача 1", "Описание 1", Status.NEW, 989898, LocalDateTime.of(2023, 2, 1, 15, 00));
    }
    protected Epic createEpic() {
        return new Epic("Epic for test", "Description of epic", Status.NEW);
    }
    // TODO: Для подзадач нужно дополнительно проверить наличие эпика, а для эпика — расчёт статуса.
    protected Subtask createSubtask(Epic epic) {
        return new Subtask("Подзадача 1 эпика 1", "Описание 1", Status.NEW, 60, LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
    }

    // history test
    @Test
    void getHistory() {
        Task task = createTask();
        taskManager.addNewTask(task);
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        List<Task> history = taskManager.getHistory();

        assertNotNull(history);
        assertEquals(3, history.size());
    }

    @Test
    void getHistoryWhenHistoryListIsEmpty() {
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    // 1. Создание объектов - add

    @Test
    void addNewTask() {
        Task task = createTask();
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1 эпика 1", "Описание 1", Status.NEW, 60, LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getListOfSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = createEpic();
        final int epicId = taskManager.addNewEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        final List<Epic> epics = taskManager.getListOfEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    // 2. Получение по индентификатору - get
    // lists of.

    @Test
    void getListOfTasks() {
        Task task = createTask();
        taskManager.addNewTask(task);
        Task task1 = createTask();
        taskManager.addNewTask(task1);

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertEquals(task1, tasks.get(1));
    }

    @Test
    void getListOfTasksWhenTasksListIsEmpty() {
        final List<Task> tasks = taskManager.getListOfTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void getListOfSubtasks() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);

        final List<Subtask> subtasks = taskManager.getListOfSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getListOfSubtasksWhenSubtasksListIsEmpty() {
        final List<Subtask> subtasks = taskManager.getListOfSubtasks();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void getListOfEpics() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Epic epic1 = createEpic();
        taskManager.addNewEpic(epic1);

        final List<Epic> epics = taskManager.getListOfEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
        assertEquals(epic1, epics.get(1));
    }


    @Test
    void getTask() { // стандартное поведение
        Task task = createTask();
        taskManager.addNewTask(task);
        assertEquals(task, taskManager.getListOfTasks().get(0),"Задачи не совпадают.");
    }

    @Test
    void getTaskWhenTasksListIsEmpty() { // попытка получения задачи из пустого списка задач
        Task task = createTask();
        assertNull(taskManager.getTask(task.getId())); // получаем null, т.к. задача была создана, но не добавлена в список
    }

    @Test
    void getTaskWithWrongId() { // попытка получения задачи с некорректным id
        Task task = createTask();
        taskManager.addNewTask(task);
        assertNull(taskManager.getTask(567));
    }

    @Test
    void getSubtask() { // стандартное поведение
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        assertEquals(subtask, taskManager.getListOfSubtasks().get(0), "Подзадачи не совпадают");
    }

    @Test
    void getSubtaskWhenSubtasksListIsEmpty() { // попытка получения подзадачи из пустого списка подзадач
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        assertNull(taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void getSubtaskWithWrongId() {
        assertNull(taskManager.getSubtask(567));
    }

    @Test
    void getEpic() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        assertEquals(epic, taskManager.getListOfEpics().get(0));
    }

    @Test
    void getEpicWhenEpicsListIsEmpty() {
        Epic epic = createEpic();
        assertNull(taskManager.getEpic(epic.getId()));
    }

    @Test
    void getEpicWithWrongId() {
        assertNull(taskManager.getEpic(567));
    }
    // 3. Обновление объектов - методы update
    @Test
    void updateTask() {
        Task task = createTask();
        taskManager.addNewTask(task);
        Task task1 = new Task(task.getId(),"UPD task name", "UPD task description", Status.DONE);
        taskManager.updateTask(task1);
        assertNotEquals(task, taskManager.getListOfTasks().get(0)); // проверили, что в список записалась обновленная задача
    }
// todo обновление с пустым списком задач и пустым идентификтором
    @Test
    void updateSubtask() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        Subtask subtask1 = new Subtask(subtask.getId(),"Подзадача 1 эпика 1", "Описание 1", Status.NEW, 60, LocalDateTime.of(2023, 9, 1, 15, 00), epic.getId());
        taskManager.updateSubtask(subtask1);
        assertNotEquals(subtask, taskManager.getListOfSubtasks().get(0));
    }

    @Test
    void updateEpic() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Epic epic1 = new Epic(epic.getId(),"UPD task name", "UPD task description",
                Status.DONE);
        taskManager.updateEpic(epic1);
        assertNotEquals(epic, taskManager.getListOfEpics().get(0));
    }
    // 4. Удаление по айди
    @Test
    void deleteTask() {
        Task task = createTask();
        taskManager.addNewTask(task);
        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getTask(task.getId()));
        assertTrue(taskManager.getListOfTasks().isEmpty());
    }
    //todo удаление задач из пустого списка
    @Test
    void deleteTaskWhenTasksListIsEmpty() { // переписать
        Task task = createTask();
        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getTask(task.getId()));
    }

    //todo удаление задач с неправильным айди
    @Test
    void deleteTaskWithWrongId() {
        Task task = createTask();
        taskManager.addNewTask(task);
        taskManager.deleteTask(-1);
        assertEquals(List.of(task), taskManager.getListOfTasks());
    }

    @Test
    void deleteSubtask() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.deleteSubtask(subtask.getId());
        assertNull(taskManager.getSubtask(subtask.getId()));
        assertTrue(taskManager.getListOfSubtasks().isEmpty());
    }
    //todo delete subtask from empty list

    @Test
    void deleteSubtaskWithWrongId() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteSubtask(-1));

        assertEquals(List.of(subtask), taskManager.getListOfSubtasks());
        assertEquals(List.of(subtask.getId()), taskManager.getEpic(epic.getId()).getSubtaskIds());

    }

    @Test
    void deleteEpic() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        taskManager.deleteEpic(epic.getId());
        assertNull(taskManager.getEpic(epic.getId()));
        assertTrue(taskManager.getListOfEpics().isEmpty());
    }
    //todo delete epic from empty list

    @Test
    void deleteEpicWithWrongId() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteEpic(-1));

        assertEquals(List.of(epic), taskManager.getListOfEpics());
    }

    //todo 5. getListOf... methods
    // todo 6. get history

    @Test
    void deleteAllTasks() {
        Task task1 = createTask();
        Task task2 = createTask();
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getListOfTasks().isEmpty());
    }

    @Test
    void deleteAllTasksWhenTasksListIsEmpty() {
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getListOfTasks().isEmpty());
    }

    @Test
    void deleteAllSubtasks() {
        Epic epic = createEpic();
        taskManager.addNewEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getListOfSubtasks().isEmpty());
    }

    @Test
    void deleteAllSubtasksWhenSubtasksListIsEmpty() {
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getListOfSubtasks().isEmpty());
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getListOfEpics().isEmpty());
    }

    @Test
    void deleteAllEpicsWhenEpicsListIsEmpty() {
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getListOfEpics().isEmpty());
    }
}