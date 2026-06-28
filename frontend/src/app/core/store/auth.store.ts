import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { AuthResponse } from '../models/auth.model';

type AuthState = {
  user: AuthResponse | null;
  isLoading: boolean;
};

const initialState: AuthState = {
  user: null,
  isLoading: false,
};

export const AuthStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store) => ({
    setUser(user: AuthResponse | null) {
      patchState(store, { user });
    },
    setLoading(isLoading: boolean) {
      patchState(store, { isLoading });
    },
    logout() {
      patchState(store, { user: null });
    }
  }))
);
