package com.example.demo.controller;

import com.example.demo.model.OldTask;
import com.example.demo.model.Task;
import com.example.demo.repository.OldTaskRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final OldTaskRepository oldTaskRepository;

    public TaskController(TaskRepository taskRepository, OldTaskRepository oldTaskRepository) {
        this.taskRepository = taskRepository;
        this.oldTaskRepository = oldTaskRepository;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/dodaj")
    public String add(Model model) {
        Task task = new Task();
        model.addAttribute("task", task);
        model.addAttribute("mode", "adding");
        return "form";
    }

    @PostMapping("/dodaj")
    public String adding(Task task) {
        taskRepository.save(task);
        return "home";
    }

    @GetMapping("/lista")
    public String showList(Model model) {
        List<Task> tasks = taskRepository.findAll();
        model.addAttribute("tasks", tasks);
        return "allTasks";
    }

    @GetMapping("/archiwum")
    public String showArchive(Model model) {
        List<OldTask> all = oldTaskRepository.findAll();
        model.addAttribute("all", all);
        return "listWithComplitedTasks";
    }

    @GetMapping("/wykonane")
    public String addToArchive(@RequestParam Long id) {
        Optional<Task> taskForDelete = taskRepository.findById(id);
        oldTaskRepository.save(new OldTask(taskForDelete.get().getName(), taskForDelete.get().getDescription(), taskForDelete.get().getDate()));
        taskRepository.deleteById(id);
        return "redirect:/lista";
    }

    @GetMapping("/kasowanie")
    public String deleteTask(@RequestParam Long id) {
        taskRepository.deleteById(id);
        return "redirect:/lista";
    }

    @GetMapping("/edytowanie")
    public String editTask(@RequestParam Long id, Model model) {
        Task oldTask = taskRepository.findById(id).orElse(null);
        Task newTask = new Task();
        model.addAttribute("task", newTask);
        model.addAttribute("oldTask", oldTask);
        model.addAttribute("mode", "edited");
        return "edit";
    }

    @PostMapping("/edytowanie")
    public String edited(@RequestParam Long id, Task newTask) {
        Task task = taskRepository.findById(id).orElse(null);
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setDate(newTask.getDate().toString());
        taskRepository.save(task);
        return "redirect:/lista";
    }
}