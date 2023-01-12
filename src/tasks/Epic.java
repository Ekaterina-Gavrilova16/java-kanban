package tasks;

import managers.impl.FileBackedTasksManager;
import managers.impl.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    LocalDateTime endTime;
    LocalDateTime startTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }


    @Override
    public LocalDateTime getStartTime() {

        List<Subtask> subtasksOfEpic = getSubtasksOfEpic(); // получили список сабтасков эпика

        if(subtasksOfEpic.isEmpty()) {
            return null;
        }
        List<LocalDateTime> listWithTime = new ArrayList<>(); // cоздали список для времени сабтасок

        for (Subtask subtask : subtasksOfEpic) { // проходимся циклом по списку сабтасок
            LocalDateTime subStartTime = subtask.getStartTime();
            listWithTime.add(subStartTime); // заполнили список времени
        }

        LocalDateTime startTime1 = listWithTime.get(0); // переменная для сравнения времени

        for (LocalDateTime dateTime : listWithTime) { // проходимся по списку времени


            if (dateTime.isBefore(startTime1) || dateTime.isEqual(startTime1)) {
                startTime = dateTime;
            }
        }
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {

        List<Subtask> subtasksOfEpic = getSubtasksOfEpic(); // получили список сабтасков эпика
        if(subtasksOfEpic.isEmpty()) {
            return null;
        }
        List<LocalDateTime> listWithTime = new ArrayList<>(); // cоздали список для времени сабтасок

            for (Subtask subtask : subtasksOfEpic) { // проходимся циклом по списку сабтасок
                LocalDateTime subEndTime = subtask.getEndTime();
                listWithTime.add(subEndTime); // заполнили список времени
            }

                LocalDateTime endTime1 = listWithTime.get(0); // переменная для сравнения времени

        for (LocalDateTime dateTime : listWithTime) { // проходимся по списку времени

            if (dateTime.isAfter(endTime1) || dateTime.isEqual(endTime1)) {
                endTime = dateTime;
            }
        }

            return endTime;

    }

    @Override
    public long getDuration() {
        try {
            Duration epicDuration = Duration.between(startTime, endTime);
            return epicDuration.toMinutes();
        } catch (NullPointerException exception) {
            return 0;
        }
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    private List<Subtask> getSubtasksOfEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for(Integer subId : subtaskIds) {
            Subtask currentSubtask = taskManager.getSubtask(subId);
            subtasksOfEpic.add(currentSubtask);
        }
        return subtasksOfEpic;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", endTime=" + endTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
