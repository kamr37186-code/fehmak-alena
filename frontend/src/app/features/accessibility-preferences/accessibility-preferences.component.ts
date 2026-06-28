import { Component, OnInit, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from "@angular/router";
import { FormsModule } from '@angular/forms';
import { PreferencesService, UserPreferences } from '../../core/services/preferences.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-accessibility-preferences',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './accessibility-preferences.component.html',
  styleUrl: './accessibility-preferences.component.css'
})
export class AccessibilityPreferencesComponent implements OnInit {

  public preferences: UserPreferences = {
    highContrastMode: false,
    colorBlindnessFilters: false,
    textScaling: 100,
    reducedMotion: false,
    aiPersona: 'Socratic Tutor'
  };

  public isSaving = false;
  public showSuccess = false;

  private destroyRef = inject(DestroyRef);

  constructor(private preferencesService: PreferencesService) {}

  ngOnInit(): void {
    // Attempt to load existing preferences if already loaded
    const current = this.preferencesService.getCurrentPreferences();
    if (current) {
      this.preferences = { ...current };
    } else {
      // Subscribe to changes if it hasn't loaded yet
      this.preferencesService.preferences$
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(prefs => {
          if (prefs) {
            this.preferences = { ...prefs };
          }
        });
    }
  }

  setPersona(persona: string): void {
    this.preferences.aiPersona = persona;
  }

  savePreferences(): void {
    this.isSaving = true;
    this.preferencesService.savePreferences(this.preferences).subscribe({
      next: () => {
        this.isSaving = false;
        this.showSuccess = true;
        setTimeout(() => this.showSuccess = false, 3000);
      },
      error: (err) => {
        this.isSaving = false;
        console.error('Error saving preferences', err);
      }
    });
  }

  resetDefaults(): void {
    this.preferences = {
      highContrastMode: false,
      colorBlindnessFilters: false,
      textScaling: 100,
      reducedMotion: false,
      aiPersona: 'Socratic Tutor'
    };
  }
}
