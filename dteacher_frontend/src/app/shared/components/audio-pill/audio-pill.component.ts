import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, signal } from '@angular/core';

// Preferred female English voices, in order of preference
const FEMALE_VOICE_NAMES = [
  'Samantha',          // macOS US — default
  'Karen',             // macOS AU
  'Victoria',          // macOS US
  'Google US English', // Chrome
  'Microsoft Zira',    // Windows US
  'Moira',             // macOS IE
  'Tessa',             // macOS ZA
];

function pickFemaleVoice(): SpeechSynthesisVoice | null {
  const voices = speechSynthesis.getVoices();
  for (const name of FEMALE_VOICE_NAMES) {
    const match = voices.find(v => v.name.startsWith(name));
    if (match) return match;
  }
  return voices.find(v => v.lang.startsWith('en')) ?? null;
}

@Component({
  selector: 'app-audio-pill',
  templateUrl: './audio-pill.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AudioPillComponent {
  text = input.required<string>();

  readonly speaking = signal(false);

  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    this.destroyRef.onDestroy(() => {
      if (this.speaking()) speechSynthesis.cancel();
    });
  }

  toggle(): void {
    if (this.speaking()) {
      speechSynthesis.cancel();
      this.speaking.set(false);
      return;
    }

    const utterance = new SpeechSynthesisUtterance(this.text());
    utterance.lang  = 'en-US';
    utterance.rate  = 0.75;
    utterance.voice = pickFemaleVoice();
    utterance.onend  = () => this.speaking.set(false);
    utterance.onerror = () => this.speaking.set(false);

    this.speaking.set(true);
    speechSynthesis.speak(utterance);
  }
}
