import { request } from "./httpClient";
import { Role } from "./types";

export type CurrentUser = {
  id: number;
  username: string;
  displayName: string;
  role: Role;
};

export type LoginRequest = {
  username: string;
  password: string;
};

export type LoginResponse = {
  token: string;
  user: CurrentUser;
};

export function login(payload: LoginRequest) {
  return request<LoginResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function me() {
  return request<CurrentUser>("/auth/me");
}

