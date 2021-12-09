import dayjs from 'dayjs';
import { IAddress } from 'app/shared/model/address.model';
import { IPayment } from 'app/shared/model/payment.model';
import { IRental } from 'app/shared/model/rental.model';

export interface ICustomer {
  id?: number;
  customerId?: number;
  storeId?: number;
  firstName?: string;
  lastName?: string;
  email?: string | null;
  activebool?: boolean;
  createDate?: string;
  lastUpdate?: string | null;
  active?: number | null;
  address?: IAddress;
  payments?: IPayment[] | null;
  rentals?: IRental[] | null;
}

export const defaultValue: Readonly<ICustomer> = {
  activebool: false,
};
