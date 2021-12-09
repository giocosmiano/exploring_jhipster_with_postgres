import dayjs from 'dayjs';
import { IFilmActor } from 'app/shared/model/film-actor.model';

export interface IActor {
  id?: number;
  actorId?: number;
  firstName?: string;
  lastName?: string;
  lastUpdate?: string;
  filmActors?: IFilmActor[] | null;
}

export const defaultValue: Readonly<IActor> = {};
