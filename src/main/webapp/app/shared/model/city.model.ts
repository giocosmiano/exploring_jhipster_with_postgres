import dayjs from 'dayjs';
import { ICountry } from 'app/shared/model/country.model';
import { IAddress } from 'app/shared/model/address.model';

export interface ICity {
  id?: number;
  cityId?: number;
  city?: string;
  lastUpdate?: string;
  country?: ICountry;
  addresses?: IAddress[] | null;
}

export const defaultValue: Readonly<ICity> = {};
