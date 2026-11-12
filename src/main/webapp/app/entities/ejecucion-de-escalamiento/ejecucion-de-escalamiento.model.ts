import dayjs from 'dayjs/esm';

import { MotivoDestinatario } from 'app/entities/enumerations/motivo-destinatario.model';
import { ResultadoEscalamiento } from 'app/entities/enumerations/resultado-escalamiento.model';
import { IIncidente } from 'app/entities/incidente/incidente.model';
import { IPasoEscalamiento } from 'app/entities/paso-escalamiento/paso-escalamiento.model';
import { IUser } from 'app/entities/user/user.model';

export interface IEjecucionDeEscalamiento {
  id: number;
  vuelta?: number | null;
  nivel?: number | null;
  motivo?: keyof typeof MotivoDestinatario | null;
  resultado?: keyof typeof ResultadoEscalamiento | null;
  ejecutadoEn?: dayjs.Dayjs | null;
  proximoEn?: dayjs.Dayjs | null;
  detalle?: string | null;
  incidente?: Pick<IIncidente, 'id' | 'titulo'> | null;
  paso?: Pick<IPasoEscalamiento, 'id' | 'orden'> | null;
  destinatario?: Pick<IUser, 'id' | 'login'> | null;
}
