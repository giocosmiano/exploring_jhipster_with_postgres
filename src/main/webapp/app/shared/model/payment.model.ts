import dayjs from 'dayjs';
import { ICustomer } from 'app/shared/model/customer.model';
import { IStaff } from 'app/shared/model/staff.model';
import { IRental } from 'app/shared/model/rental.model';

export interface IPayment {
  id?: number;
  paymentId?: number;
  amount?: number;
  paymentDate?: string;
  customer?: ICustomer;
  staff?: IStaff;
  rental?: IRental;
}

export const defaultValue: Readonly<IPayment> = {};
