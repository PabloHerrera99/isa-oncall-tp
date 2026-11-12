import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDespliegueDeServicio, NewDespliegueDeServicio } from '../despliegue-de-servicio.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDespliegueDeServicio for edit and NewDespliegueDeServicioFormGroupInput for create.
 */
type DespliegueDeServicioFormGroupInput = IDespliegueDeServicio | PartialWithRequiredKeyOf<NewDespliegueDeServicio>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDespliegueDeServicio | NewDespliegueDeServicio> = Omit<T, 'iniciadoEn' | 'finalizadoEn'> & {
  iniciadoEn?: string | null;
  finalizadoEn?: string | null;
};

type DespliegueDeServicioFormRawValue = FormValueOf<IDespliegueDeServicio>;

type NewDespliegueDeServicioFormRawValue = FormValueOf<NewDespliegueDeServicio>;

type DespliegueDeServicioFormDefaults = Pick<NewDespliegueDeServicio, 'id' | 'iniciadoEn' | 'finalizadoEn'>;

type DespliegueDeServicioFormGroupContent = {
  id: FormControl<DespliegueDeServicioFormRawValue['id'] | NewDespliegueDeServicio['id']>;
  version: FormControl<DespliegueDeServicioFormRawValue['version']>;
  entorno: FormControl<DespliegueDeServicioFormRawValue['entorno']>;
  iniciadoEn: FormControl<DespliegueDeServicioFormRawValue['iniciadoEn']>;
  finalizadoEn: FormControl<DespliegueDeServicioFormRawValue['finalizadoEn']>;
  resultado: FormControl<DespliegueDeServicioFormRawValue['resultado']>;
  cambio: FormControl<DespliegueDeServicioFormRawValue['cambio']>;
  servicio: FormControl<DespliegueDeServicioFormRawValue['servicio']>;
  ejecutadoPor: FormControl<DespliegueDeServicioFormRawValue['ejecutadoPor']>;
};

export type DespliegueDeServicioFormGroup = FormGroup<DespliegueDeServicioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DespliegueDeServicioFormService {
  createDespliegueDeServicioFormGroup(despliegueDeServicio?: DespliegueDeServicioFormGroupInput): DespliegueDeServicioFormGroup {
    const despliegueDeServicioRawValue = this.convertDespliegueDeServicioToDespliegueDeServicioRawValue({
      ...this.getFormDefaults(),
      ...(despliegueDeServicio ?? { id: null }),
    });

    return new FormGroup<DespliegueDeServicioFormGroupContent>({
      id: new FormControl(
        { value: despliegueDeServicioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      version: new FormControl(despliegueDeServicioRawValue.version, {
        validators: [Validators.required, Validators.maxLength(60)],
      }),
      entorno: new FormControl(despliegueDeServicioRawValue.entorno, {
        validators: [Validators.required],
      }),
      iniciadoEn: new FormControl(despliegueDeServicioRawValue.iniciadoEn, {
        validators: [Validators.required],
      }),
      finalizadoEn: new FormControl(despliegueDeServicioRawValue.finalizadoEn),
      resultado: new FormControl(despliegueDeServicioRawValue.resultado, {
        validators: [Validators.required],
      }),
      cambio: new FormControl(despliegueDeServicioRawValue.cambio, {
        validators: [Validators.required],
      }),
      servicio: new FormControl(despliegueDeServicioRawValue.servicio, {
        validators: [Validators.required],
      }),
      ejecutadoPor: new FormControl(despliegueDeServicioRawValue.ejecutadoPor),
    });
  }

  getDespliegueDeServicio(form: DespliegueDeServicioFormGroup): IDespliegueDeServicio | NewDespliegueDeServicio {
    return this.convertDespliegueDeServicioRawValueToDespliegueDeServicio(form.getRawValue());
  }

  resetForm(form: DespliegueDeServicioFormGroup, despliegueDeServicio: DespliegueDeServicioFormGroupInput): void {
    const despliegueDeServicioRawValue = this.convertDespliegueDeServicioToDespliegueDeServicioRawValue({
      ...this.getFormDefaults(),
      ...despliegueDeServicio,
    });
    form.reset({
      ...despliegueDeServicioRawValue,
      id: { value: despliegueDeServicioRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DespliegueDeServicioFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      iniciadoEn: currentTime,
      finalizadoEn: currentTime,
    };
  }

  private convertDespliegueDeServicioRawValueToDespliegueDeServicio(
    rawDespliegueDeServicio: DespliegueDeServicioFormRawValue | NewDespliegueDeServicioFormRawValue,
  ): IDespliegueDeServicio | NewDespliegueDeServicio {
    return {
      ...rawDespliegueDeServicio,
      iniciadoEn: dayjs(rawDespliegueDeServicio.iniciadoEn, DATE_TIME_FORMAT),
      finalizadoEn: dayjs(rawDespliegueDeServicio.finalizadoEn, DATE_TIME_FORMAT),
    };
  }

  private convertDespliegueDeServicioToDespliegueDeServicioRawValue(
    despliegueDeServicio: IDespliegueDeServicio | (Partial<NewDespliegueDeServicio> & DespliegueDeServicioFormDefaults),
  ): DespliegueDeServicioFormRawValue | PartialWithRequiredKeyOf<NewDespliegueDeServicioFormRawValue> {
    return {
      ...despliegueDeServicio,
      iniciadoEn: despliegueDeServicio.iniciadoEn ? despliegueDeServicio.iniciadoEn.format(DATE_TIME_FORMAT) : undefined,
      finalizadoEn: despliegueDeServicio.finalizadoEn ? despliegueDeServicio.finalizadoEn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
