import { FormControl, FormGroup } from '@angular/forms';
import { describe, expect, it } from 'vitest';
import { passwordsMatchValidator } from './password-match.validator';

describe('passwordsMatchValidator', () => {
  it('returns null when passwords match', () => {
    const group = new FormGroup(
      {
        password: new FormControl('secret1234'),
        confirmPassword: new FormControl('secret1234'),
      },
      { validators: passwordsMatchValidator() },
    );
    expect(group.errors).toBeNull();
  });

  it('returns passwordMismatch when passwords differ', () => {
    const group = new FormGroup(
      {
        password: new FormControl('secret1234'),
        confirmPassword: new FormControl('other'),
      },
      { validators: passwordsMatchValidator() },
    );
    expect(group.errors).toEqual({ passwordMismatch: true });
  });
});
