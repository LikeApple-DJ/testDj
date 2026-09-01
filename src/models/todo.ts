export interface CreateTodoInput {
  name: string;
  description?: string;
}

export interface Todo {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
}