import dayjs from 'dayjs';
import { ICity } from 'app/shared/model/city.model';

export interface ICountry {
  id?: number;
  countryId?: number;
  country?: string;
  lastUpdate?: string;
  cities?: ICity[] | null;
}

export const defaultValue: Readonly<ICountry> = {};
