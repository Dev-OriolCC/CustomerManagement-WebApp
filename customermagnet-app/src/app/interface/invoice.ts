export interface Invoice {
    id: number;
    invoiceNumber: string;
    services: string;
    date: Date;
    status: string;
    total: number;
}