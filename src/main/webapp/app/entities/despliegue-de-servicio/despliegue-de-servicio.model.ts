import dayjs from 'dayjs/esm';

import { ICambio } from 'app/entities/cambio/cambio.model';
import { Entorno } from 'app/entities/enumerations/entorno.model';
import { ResultadoDespliegue } from 'app/entities/enumerations/resultado-despliegue.model';
import { IServicio } from 'app/entities/servicio/servicio.model';
import { IUser } from 'app/entities/user/user.model';

export interface IDespliegueDeServicio {
  id: number;
  version?: string | null;
  entorno?: keyof typeof Entorno | null;
  iniciadoEn?: dayjs.Dayjs | null;
  finalizadoEn?: dayjs.Dayjs | null;
  resultado?: keyof typeof ResultadoDespliegue | null;
  cambio?: Pick<ICambio, 'id' | 'titulo'> | null;
  servicio?: Pick<IServicio, 'id' | 'nombre'> | null;
  ejecutadoPor?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewDespliegueDeServicio = Omit<IDespliegueDeServicio, 'id'> & { id: null };
