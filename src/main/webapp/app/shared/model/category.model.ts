import dayjs from 'dayjs';
import { IFilmCategory } from 'app/shared/model/film-category.model';

export interface ICategory {
  id?: number;
  categoryId?: number;
  name?: string;
  lastUpdate?: string;
  filmCategories?: IFilmCategory[] | null;
}

export const defaultValue: Readonly<ICategory> = {};
