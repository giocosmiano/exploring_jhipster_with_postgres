import dayjs from 'dayjs';
import { ILanguage } from 'app/shared/model/language.model';
import { IFilmActor } from 'app/shared/model/film-actor.model';
import { IFilmCategory } from 'app/shared/model/film-category.model';
import { IInventory } from 'app/shared/model/inventory.model';

export interface IFilm {
  id?: number;
  filmId?: number;
  title?: string;
  description?: string | null;
  releaseYear?: number | null;
  rentalDuration?: number;
  rentalRate?: number;
  length?: number | null;
  replacementCost?: number;
  rating?: string | null;
  lastUpdate?: string;
  specialFeatures?: string | null;
  fulltext?: string;
  language?: ILanguage;
  filmActors?: IFilmActor[] | null;
  filmCategories?: IFilmCategory[] | null;
  inventories?: IInventory[] | null;
}

export const defaultValue: Readonly<IFilm> = {};
