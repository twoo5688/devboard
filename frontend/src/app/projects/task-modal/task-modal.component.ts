import {
  ChangeDetectionStrategy,
  Component,
  effect,
  inject,
  input,
  output,
  untracked,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskStatus } from '../../models/task.models';

@Component({
  selector: 'app-task-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './task-modal.component.html',
  styleUrl: './task-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskModalComponent {
  private readonly fb = inject(FormBuilder);

  readonly open = input(false);
  readonly closed = output<void>();
  readonly submitted = output<{ title: string; description: string | null; status: TaskStatus }>();

  readonly statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];

  readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required]],
    description: [''],
    status: ['TODO' as TaskStatus, [Validators.required]],
  });

  constructor() {
    effect(() => {
      if (this.open()) {
        untracked(() => {
          this.form.reset({
            title: '',
            description: '',
            status: 'TODO',
          });
        });
      }
    });
  }

  onBackdropClick(ev: MouseEvent): void {
    if (ev.target === ev.currentTarget) {
      this.closed.emit();
    }
  }

  cancel(): void {
    this.closed.emit();
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.submitted.emit({
      title: v.title,
      description: v.description ? v.description : null,
      status: v.status,
    });
  }
}
