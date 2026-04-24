import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Project } from '../../models/project.models';
import { ProjectService } from '../../services/project.service';

@Component({
  selector: 'app-projects-list',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './projects-list.component.html',
  styleUrl: './projects-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectsListComponent {
  private readonly projectService = inject(ProjectService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  readonly projects = signal<Project[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly creating = signal(false);

  readonly createForm = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    description: [''],
  });

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.projectService.getProjects().subscribe({
      next: (data) => {
        this.projects.set(data);
        this.loading.set(false);
      },
      error: (err: Error) => {
        this.loading.set(false);
        this.errorMessage.set(err.message);
      },
    });
  }

  createProject(): void {
    this.errorMessage.set(null);
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }
    const { name, description } = this.createForm.getRawValue();
    this.creating.set(true);
    this.projectService.createProject({ name, description: description || null }).subscribe({
      next: (p) => {
        this.creating.set(false);
        this.createForm.reset();
        this.projects.update((list) => [...list, p]);
      },
      error: (err: Error) => {
        this.creating.set(false);
        this.errorMessage.set(err.message);
      },
    });
  }

  deleteProject(id: number, ev: Event): void {
    ev.stopPropagation();
    this.errorMessage.set(null);
    this.projectService.deleteProject(id).subscribe({
      next: () => {
        this.projects.update((list) => list.filter((p) => p.id !== id));
      },
      error: (err: Error) => {
        this.errorMessage.set(err.message);
      },
    });
  }

  openBoard(projectId: number): void {
    void this.router.navigate(['/projects', projectId, 'board']);
  }
}
