import dayjs from 'dayjs';
import { IAddress } from 'app/shared/model/address.model';
import { IPayment } from 'app/shared/model/payment.model';
import { IRental } from 'app/shared/model/rental.model';
import { IStore } from 'app/shared/model/store.model';

export interface IStaff {
  id?: number;
  staffId?: number;
  firstName?: string;
  lastName?: string;
  email?: string | null;
  storeId?: number;
  active?: boolean;
  username?: string;
  password?: string | null;
  lastUpdate?: string;
  pictureContentType?: string | null;
  picture?: string | null;
  address?: IAddress;
  payments?: IPayment[] | null;
  rentals?: IRental[] | null;
  stores?: IStore[] | null;
}

export const defaultValue: Readonly<IStaff> = {
  active: false,
};
