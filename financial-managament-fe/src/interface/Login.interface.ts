export interface LoginReq {
    username: string;
    password: string;
}

export interface LoginRes {
    token: string;
    refreshToken: string;
    userId: number;
    username: string;
    type: string;
}