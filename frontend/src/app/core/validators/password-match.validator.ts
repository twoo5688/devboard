import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Apply on the parent FormGroup that has `password` and `confirmPassword` controls. */
export function passwordsMatchValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    if (password === confirm) {
      return null;
    }
    return { passwordMismatch: true };
  };
}
