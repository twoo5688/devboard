export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface Task {
  id: number;
  title: string;
  description: string | null;
  status: TaskStatus;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface TaskRequest {
  title: string;
  description?: string | null;
  status?: TaskStatus;
}
