import dayjs from 'dayjs';
import { IStaff } from 'app/shared/model/staff.model';
import { IAddress } from 'app/shared/model/address.model';

export interface IStore {
  id?: number;
  storeId?: number;
  lastUpdate?: string;
  managerStaff?: IStaff;
  address?: IAddress;
}

export const defaultValue: Readonly<IStore> = {};
