import dayjs from 'dayjs/esm';

import { NivelRiesgo } from 'app/entities/enumerations/nivel-riesgo.model';
import { TipoCambio } from 'app/entities/enumerations/tipo-cambio.model';
import { IUser } from 'app/entities/user/user.model';

export interface ICambio {
  id: number;
  titulo?: string | null;
  descripcion?: string | null;
  tipo?: keyof typeof TipoCambio | null;
  riesgo?: keyof typeof NivelRiesgo | null;
  ticketUrl?: string | null;
  creadoEn?: dayjs.Dayjs | null;
  autor?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCambio = Omit<ICambio, 'id'> & { id: null };
