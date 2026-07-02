import { ApiResponse } from "./types";

const API_BASE_URL = "/api";

export class ApiError extends Error {
  constructor(
    message: string,
    public readonly code: string,
    public readonly traceId?: string
  ) {
    super(message);
  }
}

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem("authToken");
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init.headers
    }
  });

  const payload = (await response.json().catch(() => null)) as ApiResponse<T> | null;

  if (!response.ok || !payload) {
    throw new ApiError(payload?.message ?? "请求失败", payload?.code ?? "HTTP_ERROR", payload?.traceId);
  }

  if (payload.code !== "OK") {
    throw new ApiError(payload.message, payload.code, payload.traceId);
  }

  return payload.data;
}

