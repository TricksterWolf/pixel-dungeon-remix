package com.annatala.pixelponies.windows;

import com.annatala.pixelponies.android.util.GuiProperties;
import com.annatala.pixelponies.android.R;
import com.annatala.pixelponies.android.google.Iap;
import com.annatala.noosa.Game;
import com.annatala.noosa.Group;
import com.annatala.noosa.Text;
import com.annatala.pixelponies.PixelPonies;
import com.annatala.pixelponies.scenes.PixelScene;
import com.annatala.pixelponies.ui.Icons;
import com.annatala.pixelponies.ui.SystemRedButton;
import com.annatala.pixelponies.ui.Window;
import com.annatala.pixelponies.windows.elements.RankingTab;
import com.annatala.pixelponies.windows.elements.Tab;

public class WndDonate extends WndTabbed {

	private static final String RUBY   = Game.getVar(R.string.WndDonate_ruby);
	private static final String GOLD   = Game.getVar(R.string.WndDonate_gold);
	private static final String SILVER = Game.getVar(R.string.WndDonate_silver);

	private static final String DONATE = Game.getVar(R.string.WndDonate_donate);
	private static final String NOT_CONNECTED =  Game.getVar(R.string.WndDonate_notConnected);

	private static final String RUBY_DONATE = Game
			.getVar(R.string.WndDonate_rubyDonate);
	private static final String GOLD_DONATE = Game
			.getVar(R.string.WndDonate_goldDonate);
	private static final String SILVER_DONATE = Game
			.getVar(R.string.WndDonate_silverDonate);

	private static final String SILVER_DONATE_TEXT = Game
			.getVar(R.string.WndDonate_silverDonateText);
	private static final String GOLD_DONATE_TEXT = Game
			.getVar(R.string.WndDonate_goldDonateText);
	private static final String RUBY_DONATE_TEXT = Game
			.getVar(R.string.WndDonate_rubyDonateText);
	
	private static final String SILVER_DONATE_TEXT_2 = Game
			.getVar(R.string.WndDonate_silverDonateText2);
	private static final String GOLD_DONATE_TEXT_2 = Game
			.getVar(R.string.WndDonate_goldDonateText2);
	private static final String RUBY_DONATE_TEXT_2 = Game
			.getVar(R.string.WndDonate_rubyDonateText2);

	private static final int WIDTH = 130;
	private static final int HEIGHT = 145;

	private static final int BTN_HEIGHT = 20;
	private static final int BTN_WIDTH  = WIDTH;
	
	private static final int GAP = 2;
	private static final int TAB_WIDTH = 47;

	private static final Icons[] icons = { Icons.EARTH_PATRON,
			Icons.PEGASUS_PATRON, Icons.UNICORN_PATRON};
	private static final String[] title = { SILVER_DONATE, GOLD_DONATE,
			RUBY_DONATE };
	
	private static final String[] text = { SILVER_DONATE_TEXT,
			GOLD_DONATE_TEXT, RUBY_DONATE_TEXT };
	
	private static final String[] text2 = { SILVER_DONATE_TEXT_2,
			GOLD_DONATE_TEXT_2, RUBY_DONATE_TEXT_2 };

	public WndDonate() {
		super();
		String[] labels = { SILVER, GOLD, RUBY };
		Group[] pages = { new DonateTab(1), new DonateTab(2), new DonateTab(3) };

		for (int i = 0; i < pages.length; i++) {
			add(pages[i]);

			Tab tab = new RankingTab(this, labels[i], pages[i]);
			tab.setSize(TAB_WIDTH, tabHeight());
			add(tab);
		}

		resize(WIDTH,HEIGHT);

		select(1);
	}

	private class DonateTab extends Group {

		public DonateTab(final int level) {
			super();

			IconTitle tabTitle = new IconTitle(Icons.get(icons[level - 1]),
					title[level - 1]);
			tabTitle.setRect(0, 0, WIDTH, 0);
			add(tabTitle);

			float pos = tabTitle.bottom();

			pos += GAP;

			if (PixelPonies.donated() < level) {
				String price = Iap.getDonationPriceString(level);
				String btnText;
				if( price != null ) {
					btnText = DONATE + " "+ price;
				} else {
					btnText = NOT_CONNECTED;
				}
				SystemRedButton donate = new SystemRedButton(btnText) {
					@Override
					protected void onClick() {
						Iap.donate(level);
					}
				};
				
				if( price == null) {
					donate.enable(false);
				}
				
				add(donate.setRect(WIDTH - BTN_WIDTH ,HEIGHT - BTN_HEIGHT, BTN_WIDTH, BTN_HEIGHT));
			}

			Text commonText = PixelScene.createMultiline(
					Game.getVar(R.string.WndDonate_commonDonateText), GuiProperties.regularFontSize());
			commonText.maxWidth(WIDTH);
			commonText.measure();
			commonText.setPos(0, pos);
			add(commonText);
			pos += commonText.height() + GAP;
			
			Text tabText = PixelScene.createMultiline(
					text[level - 1], GuiProperties.regularFontSize());
			tabText.maxWidth(WIDTH - 10);
			tabText.hardlight( Window.TITLE_COLOR );
			tabText.measure();
			tabText.setPos(0, pos);
			add(tabText);
			
			pos += tabText.height() + GAP;
			
			Text tabText2 = PixelScene.createMultiline(
					text2[level - 1], GuiProperties.regularFontSize());
			tabText2.maxWidth(WIDTH - 10);
			tabText2.measure();
			tabText2.setPos(0, pos);
			add(tabText2);
		}
	}

}
