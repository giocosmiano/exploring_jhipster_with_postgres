import dayjs from 'dayjs';
import { ICity } from 'app/shared/model/city.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IStaff } from 'app/shared/model/staff.model';
import { IStore } from 'app/shared/model/store.model';

export interface IAddress {
  id?: number;
  addressId?: number;
  address?: string;
  address2?: string | null;
  district?: string;
  postalCode?: string | null;
  phone?: string;
  lastUpdate?: string;
  city?: ICity;
  customers?: ICustomer[] | null;
  staff?: IStaff[] | null;
  stores?: IStore[] | null;
}

export const defaultValue: Readonly<IAddress> = {};
