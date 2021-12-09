import dayjs from 'dayjs';
import { IFilm } from 'app/shared/model/film.model';
import { ICategory } from 'app/shared/model/category.model';

export interface IFilmCategory {
  id?: number;
  lastUpdate?: string;
  film?: IFilm;
  category?: ICategory;
}

export const defaultValue: Readonly<IFilmCategory> = {};
