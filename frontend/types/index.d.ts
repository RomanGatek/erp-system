export type LoginResponse = {
    accessToken: string;
    refreshToken: string;
}

export type Role = {
    id: number;
    name: string;
}

export interface User {
    id: number;
    name: string;
    email: string;
    password: string;
    roles: Role[];
    firstName: string;
    lastName: string;
    avatar: string;
    username: string;
    createdAt: Date;
    updatedAt: Date;
}

export type UserUpdate = Partial<User>;


export interface OrderItem {
    id: number;
    name: string;
    description: string;
    buyoutPrice: number;
    purchasePrice: number;
    stockedQuantity: number;
    neededQuantity: number;
    inventoryItemId: number;
    productId: number;
}


export interface Order {
    id: number;
    approvedBy: User;
    comment: string;
    cost: number;
    decisionTime: Date;
    orderItems: OrderItem[];
    sratus: 'PENDING' | 'APPROVED' | 'CANCELLED';
    orderType: 'SELL' | 'PURCHASE';
    status: 'PENDING' | 'APPROVED' | 'CANCELLED';
}

export interface Category {
    id: number;
    name: string;
    description: string;
}