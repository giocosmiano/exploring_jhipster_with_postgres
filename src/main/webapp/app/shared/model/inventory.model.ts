import dayjs from 'dayjs';
import { IFilm } from 'app/shared/model/film.model';
import { IRental } from 'app/shared/model/rental.model';

export interface IInventory {
  id?: number;
  inventoryId?: number;
  storeId?: number;
  lastUpdate?: string;
  film?: IFilm;
  rentals?: IRental[] | null;
}

export const defaultValue: Readonly<IInventory> = {};
