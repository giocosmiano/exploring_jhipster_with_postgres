import dayjs from 'dayjs';
import { IActor } from 'app/shared/model/actor.model';
import { IFilm } from 'app/shared/model/film.model';

export interface IFilmActor {
  id?: number;
  lastUpdate?: string;
  actor?: IActor;
  film?: IFilm;
}

export const defaultValue: Readonly<IFilmActor> = {};
