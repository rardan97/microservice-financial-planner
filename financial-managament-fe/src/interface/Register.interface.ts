export interface RegisterReq {
    fullName: string;
    username: string;
    password: string;
    email: string;
}

export interface RegisterRes {
    token: string;
    refreshToken: string;
    username: string;
}