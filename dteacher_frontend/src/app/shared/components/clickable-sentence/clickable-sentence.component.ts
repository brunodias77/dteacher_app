import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';

interface Token { display: string; clean: string; isWord: boolean; }

@Component({
  selector: 'app-clickable-sentence',
  templateUrl: './clickable-sentence.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ClickableSentenceComponent {
  text = input.required<string>();
  wordClicked = output<string>();

  tokens = computed<Token[]>(() =>
    this.text().split(/(\s+)/).map(part => ({
      display: part,
      clean: part.replace(/[.,!?;:()"'—]/g, ''),
      isWord: !/^\s+$/.test(part),
    }))
  );

  onClick(token: Token) {
    if (token.isWord && token.clean) this.wordClicked.emit(token.clean);
  }
}
