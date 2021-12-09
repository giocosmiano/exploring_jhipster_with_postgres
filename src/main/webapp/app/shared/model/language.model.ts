import dayjs from 'dayjs';
import { IFilm } from 'app/shared/model/film.model';

export interface ILanguage {
  id?: number;
  languageId?: number;
  name?: string;
  lastUpdate?: string;
  films?: IFilm[] | null;
}

export const defaultValue: Readonly<ILanguage> = {};
