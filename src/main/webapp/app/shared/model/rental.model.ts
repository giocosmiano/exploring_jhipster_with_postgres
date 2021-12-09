import dayjs from 'dayjs';
import { IInventory } from 'app/shared/model/inventory.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IStaff } from 'app/shared/model/staff.model';
import { IPayment } from 'app/shared/model/payment.model';

export interface IRental {
  id?: number;
  rentalId?: number;
  rentalDate?: string;
  returnDate?: string | null;
  lastUpdate?: string;
  inventory?: IInventory;
  customer?: ICustomer;
  staff?: IStaff;
  payments?: IPayment[] | null;
}

export const defaultValue: Readonly<IRental> = {};
