import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { ChangeDetectionStrategy, Component, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, RouterLink } from '@angular/router';
import { map } from 'rxjs/operators';
import { Task, TaskStatus } from '../../models/task.models';
import { TaskService } from '../../services/task.service';
import { TaskModalComponent } from '../task-modal/task-modal.component';

@Component({
  selector: 'app-kanban-board',
  imports: [DragDropModule, RouterLink, TaskModalComponent],
  templateUrl: './kanban-board.component.html',
  styleUrl: './kanban-board.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class KanbanBoardComponent {
  private readonly taskService = inject(TaskService);
  private readonly route = inject(ActivatedRoute);

  readonly todoListId = 'todoList';
  readonly inProgressListId = 'inProgressList';
  readonly doneListId = 'doneList';

  readonly projectId = toSignal(this.route.paramMap.pipe(map((p: ParamMap) => Number(p.get('id')))), {
    initialValue: 0,
  });

  readonly todoTasks = signal<Task[]>([]);
  readonly inProgressTasks = signal<Task[]>([]);
  readonly doneTasks = signal<Task[]>([]);

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly modalOpen = signal(false);

  constructor() {
    effect(() => {
      const id = this.projectId();
      if (id) {
        untracked(() => this.loadTasks(id));
      }
    });
  }

  openModal(): void {
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
  }

  onTaskCreate(body: { title: string; description: string | null; status: TaskStatus }): void {
    const pid = this.projectId();
    if (!pid) return;
    this.errorMessage.set(null);
    this.taskService.createTask(pid, body).subscribe({
      next: (task) => {
        this.closeModal();
        this.pushTaskToColumn(task);
      },
      error: (err: Error) => {
        this.errorMessage.set(err.message);
      },
    });
  }

  drop(event: CdkDragDrop<Task[]>, targetStatus: TaskStatus): void {
    const pid = this.projectId();
    if (!pid) return;

    if (event.previousContainer === event.container) {
      this.mutateColumn(targetStatus, (data) => {
        moveItemInArray(data, event.previousIndex, event.currentIndex);
      });
      return;
    }

    const prevContainerData = event.previousContainer.data;
    const containerData = event.container.data;
    const moved = prevContainerData[event.previousIndex];
    const previousStatus = moved.status;

    transferArrayItem(prevContainerData, containerData, event.previousIndex, event.currentIndex);
    moved.status = targetStatus;

    this.bumpColumnSignals();

    this.taskService
      .updateTask(pid, moved.id, {
        title: moved.title,
        description: moved.description,
        status: targetStatus,
      })
      .subscribe({
        error: () => {
          moved.status = previousStatus;
          this.loadTasks(pid);
          this.errorMessage.set('Could not update task status.');
        },
      });
  }

  deleteTask(task: Task, ev: Event): void {
    ev.stopPropagation();
    const pid = this.projectId();
    if (!pid) return;
    this.errorMessage.set(null);
    this.taskService.deleteTask(pid, task.id).subscribe({
      next: () => {
        this.removeTaskFromColumns(task.id);
      },
      error: (err: Error) => {
        this.errorMessage.set(err.message);
      },
    });
  }

  private loadTasks(projectId: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.taskService.getTasks(projectId).subscribe({
      next: (tasks) => {
        this.todoTasks.set(tasks.filter((t) => t.status === 'TODO'));
        this.inProgressTasks.set(tasks.filter((t) => t.status === 'IN_PROGRESS'));
        this.doneTasks.set(tasks.filter((t) => t.status === 'DONE'));
        this.loading.set(false);
      },
      error: (err: Error) => {
        this.loading.set(false);
        this.errorMessage.set(err.message);
      },
    });
  }

  private mutateColumn(status: TaskStatus, fn: (data: Task[]) => void): void {
    const col = this.columnSignal(status);
    col.update((data) => {
      const copy = [...data];
      fn(copy);
      return copy;
    });
  }

  private columnSignal(status: TaskStatus): typeof this.todoTasks {
    switch (status) {
      case 'TODO':
        return this.todoTasks;
      case 'IN_PROGRESS':
        return this.inProgressTasks;
      case 'DONE':
        return this.doneTasks;
    }
  }

  private bumpColumnSignals(): void {
    this.todoTasks.update((a) => [...a]);
    this.inProgressTasks.update((a) => [...a]);
    this.doneTasks.update((a) => [...a]);
  }

  private pushTaskToColumn(task: Task): void {
    this.columnSignal(task.status).update((list) => [...list, task]);
  }

  private removeTaskFromColumns(taskId: number): void {
    this.todoTasks.update((list) => list.filter((t) => t.id !== taskId));
    this.inProgressTasks.update((list) => list.filter((t) => t.id !== taskId));
    this.doneTasks.update((list) => list.filter((t) => t.id !== taskId));
  }
}
