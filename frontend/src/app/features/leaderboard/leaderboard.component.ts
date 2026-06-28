import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { GamificationService } from '../../core/services/gamification.service';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="max-w-7xl mx-auto space-y-6 pb-12 animate-fade-in">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-gray-900 tracking-tight">Global Leaderboard</h1>
        <button routerLink="/dashboard" class="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors shadow-sm">
          Back to Dashboard
        </button>
      </div>
      
      <div class="bg-white rounded-2xl border border-gray-100 p-8 shadow-sm">
        @if (isLoading) {
          <div class="flex justify-center py-12"><div class="animate-spin rounded-full h-8 w-8 border-b-2 border-brand-600"></div></div>
        } @else if (leaderboard.length === 0) {
          <div class="text-center py-12 text-gray-500">No users found on the leaderboard.</div>
        } @else {
          <div class="space-y-4 max-w-3xl mx-auto">
            @for (user of leaderboard; track user.firstName; let i = $index) {
              <div class="flex items-center justify-between p-4 rounded-xl transition-colors" [ngClass]="{'bg-gradient-to-r from-amber-50 to-amber-100/50 border border-amber-200 shadow-md transform scale-[1.02]': i === 0, 'bg-gray-50 border border-gray-100': i !== 0}">
                <div class="flex items-center gap-4">
                  <div class="w-8 flex justify-center">
                    @if (i === 0) {
                      <span class="text-2xl">🥇</span>
                    } @else if (i === 1) {
                      <span class="text-2xl">🥈</span>
                    } @else if (i === 2) {
                      <span class="text-2xl">🥉</span>
                    } @else {
                      <span class="font-bold text-gray-400 text-lg">{{ i + 1 }}</span>
                    }
                  </div>
                  <img class="w-12 h-12 rounded-full border-2" [ngClass]="{'border-amber-400 shadow-sm': i === 0, 'border-gray-200': i !== 0}" [src]="'https://ui-avatars.com/api/?name=' + user.firstName + '+' + user.lastName + '&background=random'" alt="">
                  <div>
                    <p class="text-lg font-bold text-gray-900">{{ user.firstName }} {{ user.lastName }}</p>
                    <p class="text-sm text-gray-500">Student</p>
                  </div>
                </div>
                <div class="text-right">
                  <span class="text-2xl font-black" [ngClass]="{'text-amber-600': i === 0, 'text-gray-700': i !== 0}">{{ user.totalXp }}</span>
                  <span class="text-sm text-gray-400 font-bold ml-1">XP</span>
                </div>
              </div>
            }
          </div>
        }
      </div>
    </div>
  `
})
export class LeaderboardComponent implements OnInit {
  leaderboard: any[] = [];
  isLoading = true;

  constructor(private gamificationService: GamificationService) {}

  ngOnInit() {
    this.gamificationService.getLeaderboard().subscribe({
      next: (data) => {
        this.leaderboard = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load leaderboard', err);
        this.isLoading = false;
      }
    });
  }
}
