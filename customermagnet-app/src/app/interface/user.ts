export interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    address?: string;
    phone?:string;
    title?: string;
    bio?: string;
    imageUrl?: string;
    enabled: boolean;
    non_locked: boolean;
    using_nfa: boolean;
    created_date?: Date;
    roleName: string;
    permissions: string;
}
