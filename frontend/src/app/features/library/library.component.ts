import { Component } from '@angular/core';

import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-library',
  standalone: true,
  imports: [RouterModule],
  template: `
    <div class="max-w-7xl mx-auto space-y-6 pb-12 animate-fade-in">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-gray-900 tracking-tight">My Library</h1>
        <button routerLink="/dashboard" class="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors shadow-sm">
          Back to Dashboard
        </button>
      </div>
      <div class="bg-white rounded-2xl border border-gray-100 p-12 text-center shadow-sm">
        <div class="w-20 h-20 bg-brand-50 text-brand-500 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path></svg>
        </div>
        <h2 class="text-xl font-bold text-gray-900 mb-2">Your Enrolled Courses</h2>
        <p class="text-gray-500 max-w-md mx-auto">This page is currently under construction. Soon, you will be able to see all your enrolled courses and learning materials here.</p>
      </div>
    </div>
  `
})
export class LibraryComponent {}
